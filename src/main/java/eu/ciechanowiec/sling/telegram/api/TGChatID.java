package eu.ciechanowiec.sling.telegram.api;

/**
 * ID that uniquely identifies a {@link TGChat}.
 */
public interface TGChatID {

    /**
     * ID that uniquely identifies a {@link TGChat}.
     * @return ID that uniquely identifies a {@link TGChat}
     */
    String asString();

    /**
     * ID that uniquely identifies a {@link TGChat}.
     * @return ID that uniquely identifies a {@link TGChat}
     */
    long asLong();
}
