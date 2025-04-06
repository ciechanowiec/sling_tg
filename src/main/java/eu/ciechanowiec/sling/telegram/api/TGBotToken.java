package eu.ciechanowiec.sling.telegram.api;

/**
 * Token of a {@link TGBot}.
 */
@FunctionalInterface
public interface TGBotToken {

    /**
     * Token of a {@link TGBot}.
     *
     * @return Token of a {@link TGBot}
     */
    String get();
}
