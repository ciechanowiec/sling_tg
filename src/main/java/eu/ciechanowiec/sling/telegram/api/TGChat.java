package eu.ciechanowiec.sling.telegram.api;

import javax.jcr.Node;

/**
 * Chat in Telegram.
 */
@FunctionalInterface
public interface TGChat {

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
