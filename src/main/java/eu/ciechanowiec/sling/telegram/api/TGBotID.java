package eu.ciechanowiec.sling.telegram.api;

/**
 * ID that uniquely identifies a {@link TGBot}.
 */
@FunctionalInterface
public interface TGBotID {

    /**
     * ID that uniquely identifies a {@link TGBot}.
     * @return ID that uniquely identifies a {@link TGBot}
     */
    String get();
}
