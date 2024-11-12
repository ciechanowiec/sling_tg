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
     * Name of a {@link Node} where {@link TGAudio} instances are persisted.
     */
    String AUDIOS_NODE_NAME = "audios";

    /**
     * Name of a {@link Node} where {@link TGDocument} instances are persisted.
     */
    String DOCUMENTS_NODE_NAME = "documents";

    /**
     * Name of a {@link Node} where {@link TGPhoto} instances are persisted.
     */
    String PHOTOS_NODE_NAME = "photos";

    /**
     * Name of a {@link Node} where {@link TGVideo} instances are persisted.
     */
    String VIDEOS_NODE_NAME = "videos";

    /**
     * Retrieves all {@link TGAsset} instances from this {@link TGAssets} instance.
     * @return all {@link TGAsset} instances from this {@link TGAssets} instance
     */
    Collection<T> all();
}
