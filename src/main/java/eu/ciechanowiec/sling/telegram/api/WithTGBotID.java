package eu.ciechanowiec.sling.telegram.api;

/**
 * Provides a {@link TGBotID}.
 */
@FunctionalInterface
public interface WithTGBotID {

    /**
     * {@link TGBotID}.
     *
     * @return {@link TGBotID}
     */
    TGBotID tgBotID();
}
