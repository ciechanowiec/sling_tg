package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Registrar of {@link TGBot} instances.
 */
public interface TGBotRegistrar {

    /**
     * Register a {@link TGBot}.
     * @param tgBot {@link TGBot} to be registered
     * @return {@link TGBotRegistration} of the registered {@link TGBot}
     * @throws TelegramApiException if an error occurs while registering the {@link TGBot}
     */
    TGBotRegistration registerBot(TGBot tgBot) throws TelegramApiException;

    /**
     * Unregister a {@link TGBot}.
     * @param tgBot {@link TGBot} to be unregistered
     * @return {@code true} if the {@link TGBot} was unregistered, {@code false} otherwise
     * @throws TelegramApiException if an error occurs while unregistering the {@link TGBot};
     *         among others the exception is thrown when attempted to unregister a {@link TGBot} that was not registered
     */
    boolean unregisterBot(TGBot tgBot) throws TelegramApiException;
}
