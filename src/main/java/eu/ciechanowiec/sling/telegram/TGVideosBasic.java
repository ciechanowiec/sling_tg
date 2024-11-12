package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.Assets;
import eu.ciechanowiec.sling.rocket.asset.StagedAssetReal;
import eu.ciechanowiec.sling.rocket.asset.StagedAssets;
import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
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
class TGVideosBasic implements TGAssets<TGVideo> {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<Collection<TGVideo>> source;

    TGVideosBasic(WithOriginalUpdate withOriginalUpdate, TGBot tgBot, ResourceAccess resourceAccess) {
        this.source = () -> getFromUpdate(withOriginalUpdate, tgBot);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    TGVideosBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.source = () -> getFromJCR(jcrPath, resourceAccess);
        log.trace("Initialized {}", this);
    }

    private Collection<TGVideo> getFromJCR(JCRPath jcrPath, ResourceAccess resourceAccess) {
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String jcrPathRaw = jcrPath.get();
            return Optional.ofNullable(resourceResolver.getResource(jcrPathRaw))
                           .filter(
                                   resource -> new NodeProperties(
                                           new TargetJCRPath(resource), resourceAccess
                                   ).isPrimaryType(Assets.NT_ASSETS)
                           )
                           .map(resource -> new Assets(resource, resourceAccess))
                           .map(Assets::get)
                           .orElseGet(List::of)
                           .stream()
                           .<TGVideo>map(TGVideoBasic::new)
                           .toList();
        }
    }

    private Collection<TGVideo> getFromUpdate(WithOriginalUpdate withOriginalUpdate, TGBot tgBot) {
        Update update = withOriginalUpdate.originalUpdate();
        return Optional.ofNullable(update.getMessage())
                       .flatMap(message -> Optional.ofNullable(message.getVideo()))
                       .<TGVideo>map(video -> new TGVideoBasic(video, tgBot))
                       .map(List::of)
                       .orElse(List.of());
    }

    @Override
    public Collection<TGVideo> all() {
        return source.get();
    }

    @Override
    @SuppressWarnings("PMD.UnnecessaryCast")
    public TGAssets<TGVideo> save(TargetJCRPath targetJCRPath) {
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
        return new TGVideosBasic(targetJCRPath, resourceAccess);
    }
}
