package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.*;
import eu.ciechanowiec.sneakyfun.SneakyConsumer;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@ToString
class TGAssetsNoBinariesPersistence<T extends TGAsset> implements TGAssets<T> {

    private final TGAssets<T> tgAssets;
    private final ResourceAccess resourceAccess;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Function<JCRPath, TGAssets<T>>> implementation;

    TGAssetsNoBinariesPersistence(Class<T> tgAssetType, TGAssets<T> tgAssets, ResourceAccess resourceAccess) {
        this.tgAssets = tgAssets;
        this.resourceAccess = resourceAccess;
        @SuppressWarnings("unchecked")
        Map<Class<T>, Function<JCRPath, TGAssets<T>>> implementations = Map.of(
                (Class<T>) TGAudio.class, jcrPath -> (TGAssets<T>) new TGAudiosBasic(jcrPath, resourceAccess),
                (Class<T>) TGDocument.class, jcrPath -> (TGAssets<T>) new TGDocumentsBasic(jcrPath, resourceAccess),
                (Class<T>) TGPhoto.class, jcrPath -> (TGAssets<T>) new TGPhotosBasic(jcrPath, resourceAccess),
                (Class<T>) TGVideo.class, jcrPath -> (TGAssets<T>) new TGVideosBasic(jcrPath, resourceAccess)
        );
        implementation = Optional.ofNullable(implementations.get(tgAssetType));
    }

    @Override
    public Collection<T> all() {
        return tgAssets.all();
    }

    @SneakyThrows
    @Override
    public TGAssets<T> save(TargetJCRPath targetJCRPath) {
        log.trace("Saving {} to {}", this, targetJCRPath);
        targetJCRPath.assertThatJCRPathIsFree(resourceAccess);
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            Resource resource = ResourceUtil.getOrCreateResource(
                    resourceResolver, targetJCRPath.get(), Map.of(), null, true
            );
            log.trace("Saved {}", resource);
        }
        all().forEach(
                SneakyConsumer.sneaky(
                        tgAsset -> {
                            JCRPath tgAssetPath = new TargetJCRPath(
                                    new ParentJCRPath(targetJCRPath), UUID.randomUUID()
                            );
                            try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
                                Resource resource = ResourceUtil.getOrCreateResource(
                                        resourceResolver, tgAssetPath.get(),
                                        tgAsset.tgMetadata()
                                               .set("additionalInfo", "Binaries were skipped")
                                               .set(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
                                               .allButObjectValues(),
                                        null, true
                                );
                                log.trace("Saved {}", resource);
                            }
                        }
                )
        );
        return implementation.map(impl -> impl.apply(targetJCRPath))
                .orElseThrow(() -> new IllegalArgumentException("No implementation found for " + tgAssets));
    }
}
