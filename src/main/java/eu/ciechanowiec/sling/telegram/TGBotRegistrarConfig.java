package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGBotRegistrar;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;

/**
 * Configuration of a {@link TGBotRegistrar}.
 */
@ObjectClassDefinition
public @interface TGBotRegistrarConfig {

    /**
     * Values for {@link GetUpdates#getAllowedUpdates()}.
     *
     * @return values for {@link GetUpdates#getAllowedUpdates()}
     */
    @AttributeDefinition(
        name = "Allowed Updates",
        description = "Values for org.telegram.telegrambots.meta.api.methods.updates.GetUpdates#getAllowedUpdates()",
        defaultValue = {
            AllowedUpdates.MESSAGE,
            AllowedUpdates.EDITEDMESSAGE,
            AllowedUpdates.CHANNELPOST,
            AllowedUpdates.EDITEDCHANNELPOST,
            AllowedUpdates.BUSINESSCONNECTION,
            AllowedUpdates.BUSINESSMESSAGE,
            AllowedUpdates.EDITEDBUSINESSMESSAGE,
            AllowedUpdates.DELETEDBUSINESSMESSAGES,
            AllowedUpdates.MESSAGEREACTION,
            AllowedUpdates.MESSAGEREACTIONCOUNT,
            AllowedUpdates.INLINEQUERY,
            AllowedUpdates.CHOSENINLINERESULT,
            AllowedUpdates.CALLBACKQUERY,
            AllowedUpdates.SHIPPINGQUERY,
            AllowedUpdates.PRECHECKOUTQUERY,
            AllowedUpdates.POLL,
            AllowedUpdates.POLLANSWER,
            AllowedUpdates.MYCHATMEMBER,
            AllowedUpdates.CHATMEMBER,
            AllowedUpdates.CHATJOINREQUEST,
            AllowedUpdates.CHATBOOST,
            AllowedUpdates.REMOVEDCHATBOOST
        },
        type = AttributeType.STRING
    )
    String[] allowed$_$updates() default {
        AllowedUpdates.MESSAGE,
        AllowedUpdates.EDITEDMESSAGE,
        AllowedUpdates.CHANNELPOST,
        AllowedUpdates.EDITEDCHANNELPOST,
        AllowedUpdates.BUSINESSCONNECTION,
        AllowedUpdates.BUSINESSMESSAGE,
        AllowedUpdates.EDITEDBUSINESSMESSAGE,
        AllowedUpdates.DELETEDBUSINESSMESSAGES,
        AllowedUpdates.MESSAGEREACTION,
        AllowedUpdates.MESSAGEREACTIONCOUNT,
        AllowedUpdates.INLINEQUERY,
        AllowedUpdates.CHOSENINLINERESULT,
        AllowedUpdates.CALLBACKQUERY,
        AllowedUpdates.SHIPPINGQUERY,
        AllowedUpdates.PRECHECKOUTQUERY,
        AllowedUpdates.POLL,
        AllowedUpdates.POLLANSWER,
        AllowedUpdates.MYCHATMEMBER,
        AllowedUpdates.CHATMEMBER,
        AllowedUpdates.CHATJOINREQUEST,
        AllowedUpdates.CHATBOOST,
        AllowedUpdates.REMOVEDCHATBOOST
    };

    /**
     * {@link String} representation of the {@link TelegramUrl} with the address of the Telegram Bot API. Syntax:
     * {@code <scheme>://<host>:<port>}. By default, this is {@link TelegramUrl#DEFAULT_URL}, i.e.
     * <a href="https://api.telegram.org:443">https://api.telegram.org:443</a>.
     *
     * @return {@link String} representation of the {@link TelegramUrl} with the address of the Telegram Bot API.
     * Syntax: {@code <scheme>://<host>:<port>}. By default, this is {@link TelegramUrl#DEFAULT_URL}, i.e.
     * <a href="https://api.telegram.org:443">https://api.telegram.org:443</a>
     */
    @AttributeDefinition(
        name = "Telegram URL",
        description = "String representation of the TelegramUrl with the address of the Telegram Bot API. "
            + "Syntax: <scheme>://<host>:<port>. By default, this is TelegramUrl#DEFAULT_URL, i.e. "
            + "https://api.telegram.org:443.",
        defaultValue = "https://api.telegram.org:443",
        type = AttributeType.STRING
    )
    String telegram_url() default "https://api.telegram.org:443";
}
