package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.FileMetadata;
import eu.ciechanowiec.sling.telegram.api.TGFile;
import eu.ciechanowiec.sling.telegram.api.TGMetadata;
import eu.ciechanowiec.sling.telegram.api.TGPhoto;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGPhotoBasic implements TGPhoto {

    @ToString.Exclude
    private final Supplier<TGFile> tgFileSupplier;
    @ToString.Exclude
    private final Supplier<TGMetadata> tgMetadataSupplier;
    @ToString.Exclude
    private final Supplier<Optional<Asset>> assetSupplier;

    TGPhotoBasic(Asset asset) {
        tgFileSupplier = () -> () -> asset.assetFile().retrieve();
        tgMetadataSupplier = () -> new TGMetadataBasic(asset);
        assetSupplier = () -> Optional.of(asset);
        log.trace("Initialized {}", this);
    }

    TGPhotoBasic(File file) {
        tgFileSupplier = () -> () -> Optional.of(file);
        tgMetadataSupplier = () -> new TGMetadataBasic(new FileMetadata(file));
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
