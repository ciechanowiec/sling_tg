package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.image.ComparableImage;
import eu.ciechanowiec.sling.rocket.asset.image.ComparableImages;
import eu.ciechanowiec.sling.telegram.api.TGOutputGate;
import eu.ciechanowiec.sling.telegram.api.WithOriginalUpdate;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
class PhotosFromTGUpdate {

    private final Update update;
    private final TGOutputGate tgOutputGate;

    PhotosFromTGUpdate(WithOriginalUpdate withOriginalUpdate, TGOutputGate tgOutputGate) {
        this.update = withOriginalUpdate.originalUpdate();
        this.tgOutputGate = tgOutputGate;
    }

    @SuppressWarnings({"LoggingSimilarMessage", "SameParameterValue"})
    Collection<File> retrieve(boolean excludeSimilar) {
        log.trace("Retrieving photos from {}. Exclude similar: {}", update, excludeSimilar);
        List<File> allRetrievedPhotos = retrieve(update, tgOutputGate);
        if (excludeSimilar) {
            List<ComparableImage> comparableImages = allRetrievedPhotos.stream()
                    .map(ComparableImage::new)
                    .toList();
            Collection<File> onlyUniqueImages = excludeSimilarImages(comparableImages);
            log.trace("Retrieved photos that will be returned: {}. Update: {}", onlyUniqueImages, update);
            return onlyUniqueImages;
        } else {
            log.trace("Retrieved photos that will be returned: {}. Update: {}", allRetrievedPhotos, update);
            return allRetrievedPhotos;
        }
    }

    private List<File> retrieve(Update update, TGOutputGate tgOutputGate) {
        return Optional.ofNullable(update.getMessage())
                       .flatMap(message -> Optional.ofNullable(message.getPhoto()))
                       .map(photoSizes -> retrieve(photoSizes, tgOutputGate))
                       .orElse(List.of());
    }

    private List<File> retrieve(Collection<PhotoSize> photoSizes, TGOutputGate tgOutputGate) {
        return photoSizes.stream()
                .map(PhotoSize::getFileId)
                .map(GetFile::new)
                .map(getFile -> CompletableFuture.supplyAsync(() -> tgOutputGate.execute(getFile, true)))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        futures -> CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                .thenApply(
                                        voidFromAll -> futures.stream()
                                                .map(CompletableFuture::join)
                                                .flatMap(Optional::stream)
                                                .toList()
                                )
                                .join()
                ));
    }

    private Collection<File> excludeSimilarImages(Collection<ComparableImage> comparableImages) {
        return new ComparableImages(comparableImages).excludeSimilarImages().asFiles();
    }
}
