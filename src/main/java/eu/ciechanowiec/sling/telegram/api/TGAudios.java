package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

import javax.jcr.Node;
import java.util.Collection;

/**
 * Collection of {@link TGAudio} instances.
 */
public interface TGAudios extends StagedNode<TGAudios> {

    /**
     * Name of a {@link Node} where {@link TGAudios} are persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String AUDIOS_NODE_NAME = "audios";

    /**
     * Retrieves all {@link TGAudio} instances from this {@link TGAudios} instance.
     * @return all {@link TGAudio} instances from this {@link TGAudios} instance
     */
    Collection<TGAudio> all();
}
