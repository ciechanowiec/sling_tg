package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.FileMetadata;
import eu.ciechanowiec.sling.rocket.commons.MemoizingSupplier;
import eu.ciechanowiec.sling.rocket.unit.DataSize;
import eu.ciechanowiec.sling.rocket.unit.DataUnit;
import eu.ciechanowiec.sling.telegram.api.TGAudio;
import eu.ciechanowiec.sling.telegram.api.TGBot;
import eu.ciechanowiec.sling.telegram.api.TGDocument;
import eu.ciechanowiec.sling.telegram.api.TGFile;
import eu.ciechanowiec.sling.telegram.api.TGIOGate;
import eu.ciechanowiec.sling.telegram.api.TGMetadata;
import eu.ciechanowiec.sling.telegram.api.TGPhoto;
import eu.ciechanowiec.sling.telegram.api.TGVideo;
import eu.ciechanowiec.sling.telegram.api.TGVoice;
import eu.ciechanowiec.sneakyfun.SneakyFunction;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;

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
        tgFileSupplier = () -> new TGFileFromAssetFile(asset.assetFile());
        tgMetadataSupplier = () -> new TGMetadataBasic(asset);
        assetSupplier = () -> Optional.of(asset);
        log.trace("Initialized {}", this);
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
            public InputStream retrieve() {
                return fileSupplier.get()
                    .map(SneakyFunction.sneaky(file -> Files.newInputStream(file.toPath())))
                    .orElse(InputStream.nullInputStream());
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
