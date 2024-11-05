package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.longpolling.BotSession;

/**
 * Registration of a {@link TGBot} instance.
 */
public interface TGBotRegistration {

    /**
     * {@link TGIOGate} of a registered {@link TGBot}.
     * @return {@link TGIOGate} of a registered {@link TGBot}
     */
    TGIOGate tgIOGate();

    /**
     * {@link BotSession} of a registered {@link TGBot}.
     * @return {@link BotSession} of a registered {@link TGBot}
     */
    BotSession botSession();
}
