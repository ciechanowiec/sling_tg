package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.objects.Video;

import javax.jcr.Node;

/**
 * Proxy of a {@link Video}.
 */
@SuppressWarnings({"InterfaceIsType", "PMD.ConstantsInInterface"})
public interface TGVideo extends TGAsset {

    /**
     * Name of a {@link Node} where the {@link TGVideo} instance is persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String VIDEO_NODE_NAME = "video";
}
