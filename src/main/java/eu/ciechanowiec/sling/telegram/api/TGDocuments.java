package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

import javax.jcr.Node;
import java.util.Collection;

/**
 * Collection of {@link TGDocument} instances.
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public interface TGDocuments extends StagedNode<TGDocuments> {

    /**
     * Name of a {@link Node} where {@link TGDocuments} are persisted.
     */
    String DOCUMENTS_NODE_NAME = "documents";

    /**
     * Retrieves all {@link TGDocument} instances from this {@link TGDocuments} instance.
     * @return all {@link TGDocument} instances from this {@link TGDocuments} instance
     */
    Collection<TGDocument> all();
}
