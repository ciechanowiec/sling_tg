package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

import javax.jcr.Node;
import java.util.Collection;

/**
 * Collection of {@link TGPhoto} instances.
 */
public interface TGPhotos extends StagedNode<TGPhotos> {

    /**
     * Name of a {@link Node} where {@link TGPhotos} are persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String PHOTOS_NODE_NAME = "photos";

    /**
     * Retrieves all {@link TGPhoto} instances from this {@link TGPhotos} instance.
     * @return all {@link TGPhoto} instances from this {@link TGPhotos} instance
     */
    Collection<TGPhoto> all();
}
