package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.meta.TelegramUrl;

/**
 * Bot in Telegram.
 */
public interface TGBot extends WithTGBotID {

    /**
     * {@link TGBotHome} related with this {@link TGBot}.
     * @return {@link TGBotHome} related with this {@link TGBot}
     */
    TGBotHome tgBotHome();

    /**
     * {@link TGBotToken} related with this {@link TGBot}.
     * @return {@link TGBotToken} related with this {@link TGBot}
     */
    TGBotToken tgBotToken();

    /**
     * {@link TGIOGate} related with this {@link TGBot}.
     * @return {@link TGIOGate} related with this {@link TGBot}
     */
    TGIOGate tgIOGate();

    /**
     * {@link TGCommands} related with this {@link TGBot}.
     * @return {@link TGCommands} related with this {@link TGBot}
     */
    TGCommands tgCommands();

    /**
     * {@link BotSession} related with this {@link TGBot}.
     * @return {@link BotSession} related with this {@link TGBot}
     */
    BotSession botSession();

    /**
     * {@link TelegramUrl} with the address of the Telegram Bot API that should be used by this {@link TGBot}.
     * @return {@link TelegramUrl} with the address of the Telegram Bot API that should be used by this {@link TGBot}
     */
    TelegramUrl telegramUrl();
}
