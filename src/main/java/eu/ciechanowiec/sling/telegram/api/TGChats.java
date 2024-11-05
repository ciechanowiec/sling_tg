package eu.ciechanowiec.sling.telegram.api;

/**
 * Collection of {@link TGChat} instances.
 */
@FunctionalInterface
public interface TGChats {

    /**
     * Get or create a {@link TGChat} instance.
     * @param withTGChatID entity with an ID that uniquely identifies the retrieved {@link TGChat}
     * @param withTGBotID entity with an ID that uniquely identifies a {@link TGBot}
     *                    that is supposed to be associated the retrieved {@link TGChat}
     * @return an instance of an existing {@link TGChat};
     *         if it doesn't exist yet, a new instance is created and returned
     */
    TGChat getOrCreate(WithTGChatID withTGChatID, WithTGBotID withTGBotID);
}
