package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.FileMetadata;
import eu.ciechanowiec.sling.telegram.api.*;
import jakarta.ws.rs.core.MediaType;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGVideoBasic implements TGVideo {

    @ToString.Exclude
    private final Supplier<TGFile> tgFileSupplier;
    @ToString.Exclude
    private final Supplier<TGMetadata> tgMetadataSupplier;
    @ToString.Exclude
    private final Supplier<Optional<Asset>> assetSupplier;

    TGVideoBasic(Asset asset) {
        tgFileSupplier = () -> () -> asset.assetFile().retrieve();
        tgMetadataSupplier = () -> new TGMetadataBasic(asset);
        assetSupplier = () -> Optional.of(asset);
        log.trace("Initialized {}", this);
    }

    TGVideoBasic(Video video, TGBot tgBot) {
        CompletableFuture<Optional<File>> fileFuture = CompletableFuture.supplyAsync(() -> {
            String fileId = video.getFileId();
            GetFile getFile = new GetFile(fileId);
            TGIOGate tgIOGate = tgBot.tgIOGate();
            return tgIOGate.execute(getFile, true);
        });
        tgFileSupplier = () -> fileFuture::join;
        tgMetadataSupplier = () -> new TGMetadataBasic(
                video,
                () -> fileFuture.join().map(FileMetadata::new).map(FileMetadata::mimeType).orElse(MediaType.WILDCARD)
        );
        assetSupplier = Optional::empty;
        log.trace("Initialized {}", this);
    }

    @Override
    public TGFile tgFile() {
        return tgFileSupplier.get();
    }

    @Override
    public TGMetadata tgMetadata() {
        return tgMetadataSupplier.get();
    }

    @Override
    public Optional<Asset> asset() {
        return assetSupplier.get();
    }
}
