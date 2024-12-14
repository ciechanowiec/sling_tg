package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.objects.Audio;

import javax.jcr.Node;

/**
 * Proxy of an {@link Audio}.
 */
@SuppressWarnings({"InterfaceIsType", "PMD.ConstantsInInterface"})
public interface TGAudio extends TGAsset {

    /**
     * Name of a {@link Node} where the {@link TGAudio} instance is persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String AUDIO_NODE_NAME = "audio";
}
