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
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGAudiosBasic implements TGAudios {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<Collection<TGAudio>> source;

    TGAudiosBasic(WithOriginalUpdate withOriginalUpdate, TGBot tgBot, ResourceAccess resourceAccess) {
        this.source = () -> getFromUpdate(withOriginalUpdate, tgBot);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    TGAudiosBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.source = () -> getFromJCR(jcrPath, resourceAccess);
        log.trace("Initialized {}", this);
    }

    private Collection<TGAudio> getFromJCR(JCRPath jcrPath, ResourceAccess resourceAccess) {
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String jcrPathRaw = jcrPath.get();
            return Optional.ofNullable(resourceResolver.getResource(jcrPathRaw))
                           .map(resource -> new Assets(resource, resourceAccess))
                           .map(Assets::get)
                           .orElseGet(List::of)
                           .stream()
                           .<TGAudio>map(TGAudioBasic::new)
                           .toList();
        }
    }

    private Collection<TGAudio> getFromUpdate(WithOriginalUpdate withOriginalUpdate, TGBot tgBot) {
        Update update = withOriginalUpdate.originalUpdate();
        return Optional.ofNullable(update.getMessage())
                       .flatMap(message -> Optional.ofNullable(message.getAudio()))
                       .<TGAudio>map(audio -> new TGAudioBasic(audio, tgBot))
                       .map(List::of)
                       .orElse(List.of());
    }

    @Override
    public Collection<TGAudio> all() {
        return source.get();
    }

    @Override
    @SuppressWarnings("PMD.UnnecessaryCast")
    public TGAudios save(TargetJCRPath targetJCRPath) {
        log.trace("Saving {} to {}", this, targetJCRPath);
        targetJCRPath.assertThatJCRPathIsFree(resourceAccess);
        List<StagedNode<Asset>> stagedAssetsRaw = all().stream().map(
                tgVideo -> {
                    TGFile tgFile = tgVideo.tgFile();
                    TGMetadata tgMetaData = tgVideo.tgMetadata();
                    return (StagedNode<Asset>) new StagedAssetReal(tgFile, tgMetaData, resourceAccess);
                }
        ).toList();
        Assets assets = new StagedAssets(stagedAssetsRaw, resourceAccess).save(targetJCRPath);
        log.trace("Saved {} to {}", assets, targetJCRPath);
        return new TGAudiosBasic(targetJCRPath, resourceAccess);
    }
}
