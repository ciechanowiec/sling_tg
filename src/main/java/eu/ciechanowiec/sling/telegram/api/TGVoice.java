package eu.ciechanowiec.sling.telegram.api;

import javax.jcr.Node;
import org.telegram.telegrambots.meta.api.objects.Voice;

/**
 * Proxy of a {@link Voice}.
 */
@SuppressWarnings({"InterfaceIsType", "PMD.ConstantsInInterface"})
public interface TGVoice extends TGAsset {

    /**
     * Name of a {@link Node} where the {@link TGVoice} instance is persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String VOICE_NODE_NAME = "voice";
}
