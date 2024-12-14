package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.StagedAssetReal;
import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.StagedNode;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGAsset;
import eu.ciechanowiec.sling.telegram.api.TGFile;
import eu.ciechanowiec.sling.telegram.api.TGMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
class SaveTGAsset implements StagedNode<Optional<TGAsset>> {

    private final ResourceAccess resourceAccess;
    private final TGAsset tgAssetToSave;

    SaveTGAsset(TGAsset tgAssetToSave, ResourceAccess resourceAccess) {
        this.tgAssetToSave = tgAssetToSave;
        this.resourceAccess = resourceAccess;
    }

    @Override
    public Optional<TGAsset> save(TargetJCRPath targetJCRPath) {
        log.trace("Saving {} to {}", this, targetJCRPath);
        targetJCRPath.assertThatJCRPathIsFree(resourceAccess);
        return tgAssetToSave.tgFile().retrieve().map(file -> {
            TGFile tgFile = tgAssetToSave.tgFile();
            TGMetadata tgMetadata = tgAssetToSave.tgMetadata();
            Asset savedAsset = new StagedAssetReal(tgFile, tgMetadata, resourceAccess).save(targetJCRPath);
            return Optional.of((TGAsset) new TGAssetBasic(savedAsset));
        }).orElseGet(() -> {
            log.debug("No file to save in {} at {}", this, targetJCRPath);
            return Optional.empty();
        });
    }
}
