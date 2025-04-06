package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Proxy of an {@link Update}.
 */
public interface TGUpdate extends WithOriginalUpdate, WithTGChatID {

    /**
     * {@link TGBot} that received this {@link TGUpdate}.
     *
     * @return {@link TGBot} that received this {@link TGUpdate}
     */
    TGBot tgBot();

    /**
     * {@link TGMessage} associated with this {@link TGUpdate}.
     *
     * @return {@link TGMessage} associated with this {@link TGUpdate}
     */
    TGMessage tgMessage();

    /**
     * Creates a new instance of a {@link TGUpdate} based upon this instance of the {@link TGUpdate}, but with a new
     * {@link TGMessage} associated with the new instance of the {@link TGUpdate}.
     *
     * @param newMessage {@link TGMessage} that will be associated with the new instance of the {@link TGUpdate}
     * @return new instance of a {@link TGUpdate} based upon this instance of the {@link TGUpdate}, but with a new
     * {@link TGMessage} associated with the new instance of the {@link TGUpdate}
     */
    TGUpdate withNewMessage(TGMessage newMessage);
}
