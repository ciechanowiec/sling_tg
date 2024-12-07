package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.path.WithJCRPath;

import javax.jcr.Node;

/**
 * Chat in Telegram.
 */
public interface TGChat extends WithJCRPath {

    /**
     * Name of a {@link Node} where a {@link TGChat} is persisted.
     */
    String MESSAGES_NODE_NAME = "messages";

    /**
     * {@link TGMessages} of this {@link TGChat}.
     * @return {@link TGMessages} of this {@link TGChat}
     */
    TGMessages tgMessages();
}
