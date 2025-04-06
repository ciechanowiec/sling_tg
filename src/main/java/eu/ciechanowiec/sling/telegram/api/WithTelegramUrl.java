package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.TelegramUrl;

/**
 * Provides a {@link TelegramUrl} with the address of the Telegram Bot API that should be used by the related entity.
 */
@FunctionalInterface
public interface WithTelegramUrl {

    /**
     * {@link TelegramUrl} with the address of the Telegram Bot API that should be used by the related entity.
     *
     * @return {@link TelegramUrl} with the address of the Telegram Bot API that should be used by the related entity.
     */
    TelegramUrl telegramUrl();
}
