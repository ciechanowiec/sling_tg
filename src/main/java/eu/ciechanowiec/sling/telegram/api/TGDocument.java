package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.objects.Document;

import javax.jcr.Node;

/**
 * Proxy of a {@link Document}.
 */
@SuppressWarnings({"InterfaceIsType", "PMD.ConstantsInInterface"})
public interface TGDocument extends TGAsset {

    /**
     * Name of a {@link Node} where the {@link TGDocument} instance is persisted.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    String DOCUMENT_NODE_NAME = "document";
}
