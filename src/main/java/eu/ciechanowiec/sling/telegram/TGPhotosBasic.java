package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.Assets;
import eu.ciechanowiec.sling.rocket.asset.StagedAssetReal;
import eu.ciechanowiec.sling.rocket.asset.StagedAssets;
import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.StagedNode;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.*;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGPhotosBasic implements TGPhotos {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<Collection<TGPhoto>> source;

    TGPhotosBasic(WithOriginalUpdate withOriginalUpdate, TGBot tgBot, ResourceAccess resourceAccess) {
        this.source = () -> getFromUpdate(withOriginalUpdate, tgBot);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    TGPhotosBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.source = () -> getFromJCR(jcrPath, resourceAccess);
        log.trace("Initialized {}", this);
    }

    private Collection<TGPhoto> getFromJCR(JCRPath jcrPath, ResourceAccess resourceAccess) {
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String jcrPathRaw = jcrPath.get();
            return Optional.ofNullable(resourceResolver.getResource(jcrPathRaw))
                           .map(resource -> new Assets(resource, resourceAccess))
                           .map(Assets::get)
                           .orElseGet(List::of)
                           .stream()
                           .<TGPhoto>map(TGPhotoBasic::new)
                           .toList();
        }
    }

    private Collection<TGPhoto> getFromUpdate(WithOriginalUpdate withOriginalUpdate, TGBot tgBot) {
        TGIOGate tgIOGate = tgBot.tgIOGate();
        PhotosFromTGUpdate photosFromUpdate = new PhotosFromTGUpdate(withOriginalUpdate, tgIOGate);
        Collection<File> photosRetrieved = photosFromUpdate.retrieve(true);
        return photosRetrieved.stream()
                              .<TGPhoto>map(TGPhotoBasic::new)
                              .toList();
    }

    @Override
    public Collection<TGPhoto> all() {
        return source.get();
    }

    @Override
    @SuppressWarnings("PMD.UnnecessaryCast")
    public TGPhotos save(TargetJCRPath targetJCRPath) {
        log.trace("Saving {} to {}", this, targetJCRPath);
        targetJCRPath.assertThatJCRPathIsFree(resourceAccess);
        List<StagedNode<Asset>> stagedAssetsRaw = all().stream().map(
                tgPhoto -> {
                    TGFile tgFile = tgPhoto.tgFile();
                    TGMetadata tgMetaData = tgPhoto.tgMetadata();
                    return (StagedNode<Asset>) new StagedAssetReal(tgFile, tgMetaData, resourceAccess);
                }
        ).toList();
        Assets assets = new StagedAssets(stagedAssetsRaw, resourceAccess).save(targetJCRPath);
        log.trace("Saved {} to {}", assets, targetJCRPath);
        return new TGPhotosBasic(targetJCRPath, resourceAccess);
    }
}