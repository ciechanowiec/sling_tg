package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

import javax.jcr.Node;
import java.util.Collection;

/**
 * Collection of {@link TGAsset} instances.
 * @param <T> type of wrapped {@link TGAsset} instances
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public interface TGAssets<T extends TGAsset> extends StagedNode<TGAssets<T>> {

    /**
     * Name of a {@link Node} where {@link TGPhoto} instances are persisted.
     */
    String PHOTOS_NODE_NAME = "photos";

    /**
     * Retrieves all {@link TGAsset} instances from this {@link TGAssets} instance.
     * @return all {@link TGAsset} instances from this {@link TGAssets} instance
     */
    Collection<T> all();
}
