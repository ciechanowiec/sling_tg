package eu.ciechanowiec.sling.telegram.api;

/**
 * Provides a {@link TGBotToken}.
 */
@FunctionalInterface
public interface WithTGBotToken {

    /**
     * {@link TGBotToken}.
     *
     * @return {@link TGBotToken}
     */
    TGBotToken tgBotToken();
}
