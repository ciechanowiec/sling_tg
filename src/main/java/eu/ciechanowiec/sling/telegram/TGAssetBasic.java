package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.FileMetadata;
import eu.ciechanowiec.sling.rocket.commons.MemoizingSupplier;
import eu.ciechanowiec.sling.rocket.unit.DataSize;
import eu.ciechanowiec.sling.rocket.unit.DataUnit;
import eu.ciechanowiec.sling.telegram.api.*;
import jakarta.ws.rs.core.MediaType;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGAssetBasic implements TGAudio, TGDocument, TGPhoto, TGVideo, TGVoice {

    @ToString.Exclude
    private final Supplier<TGFile> tgFileSupplier;
    @ToString.Exclude
    private final Supplier<TGMetadata> tgMetadataSupplier;
    @ToString.Exclude
    private final Supplier<Optional<Asset>> assetSupplier;

    TGAssetBasic(Asset asset) {
        tgFileSupplier = () -> () -> asset.assetFile().retrieve();
        tgMetadataSupplier = () -> new TGMetadataBasic(asset);
        assetSupplier = () -> Optional.of(asset);
        log.trace("Initialized {}", this);
    }

    TGAssetBasic(Audio audio, TGBot tgBot) {
        this(new WithOriginalMetadata(audio), tgBot);
    }

    TGAssetBasic(Document document, TGBot tgBot) {
        this(new WithOriginalMetadata(document), tgBot);
    }

    TGAssetBasic(Video video, TGBot tgBot) {
        this(new WithOriginalMetadata(video), tgBot);
    }

    TGAssetBasic(Supplier<TGFile> tgFileSupplier, Supplier<TGMetadata> tgMetadataSupplier) {
        this.tgFileSupplier = tgFileSupplier;
        this.tgMetadataSupplier = tgMetadataSupplier;
        this.assetSupplier = Optional::empty;
        log.trace("Initialized {}", this);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    TGAssetBasic(WithOriginalMetadata withOriginalMetadata, TGBot tgBot) {
        MemoizingSupplier<Optional<File>> fileSupplier = new MemoizingSupplier<>(
                () -> {
                    String fileId = withOriginalMetadata.fileID();
                    GetFile getFile = new GetFile(fileId);
                    TGIOGate tgIOGate = tgBot.tgIOGate();
                    return tgIOGate.execute(getFile, true);
                }
        );
        tgFileSupplier = () -> new TGFile() {
            @Override
            public Optional<File> retrieve() {
                return fileSupplier.get();
            }

            @Override
            public DataSize size() {
                if (fileSupplier.wasComputed()) {
                    return fileSupplier.get()
                            .map(file -> new DataSize(() -> file))
                            .or(withOriginalMetadata::originalDataSize)
                            .orElse(new DataSize(0, DataUnit.BYTES));
                } else {
                    return withOriginalMetadata.originalDataSize().orElse(new DataSize(0, DataUnit.BYTES));
                }
            }
        };
        tgMetadataSupplier = () -> new TGMetadataBasic(
                withOriginalMetadata,
                () -> fileSupplier.get().map(FileMetadata::new).map(FileMetadata::mimeType).orElse(MediaType.WILDCARD)
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
