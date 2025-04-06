package eu.ciechanowiec.sling.telegram.api;

/**
 * Provides a {@link TGChatID}.
 */
@FunctionalInterface
public interface WithTGChatID {

    /**
     * {@link TGChatID}.
     *
     * @return {@link TGChatID}
     */
    TGChatID tgChatID();
}
