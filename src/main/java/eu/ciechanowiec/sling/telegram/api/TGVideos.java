package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

import javax.jcr.Node;
import java.util.Collection;

/**
 * Collection of {@link TGVideo} instances.
 */
public interface TGVideos extends StagedNode<TGVideos> {

    /**
     * Name of a {@link Node} where {@link TGVideos} are persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String VIDEOS_NODE_NAME = "videos";

    /**
     * Retrieves all {@link TGVideo} instances from this {@link TGVideos} instance.
     * @return all {@link TGVideo} instances from this {@link TGVideos} instance
     */
    Collection<TGVideo> all();
}
