package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.telegram.api.TGBot;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.telegram.telegrambots.meta.TelegramUrl;

/**
 * Configuration of a {@link TGBot}.
 */
@ObjectClassDefinition
public @interface TGBotConfig {

    /**
     * Bot token issued by Telegram
     *
     * @return bot token issued by Telegram
     */
    @AttributeDefinition(
        name = "Bot Token",
        description = "Bot token issued by Telegram",
        defaultValue = StringUtils.EMPTY,
        type = AttributeType.PASSWORD
    )
    String token() default StringUtils.EMPTY;

    /**
     * Unique ID for the {@link TGBot}
     *
     * @return unique ID for the {@link TGBot}
     */
    @AttributeDefinition(
        name = "Unique ID",
        description = "Unique ID for the bot",
        defaultValue = StringUtils.EMPTY,
        type = AttributeType.STRING
    )
    String id() default StringUtils.EMPTY;

    /**
     * {@link TGBot} name
     *
     * @return {@link TGBot} name
     */
    @AttributeDefinition(
        name = "Bot Name",
        description = "Bot name",
        defaultValue = StringUtils.EMPTY,
        type = AttributeType.STRING
    )
    String name() default StringUtils.EMPTY;

    /**
     * {@link TGBot} description
     *
     * @return {@link TGBot} description
     */
    @AttributeDefinition(
        name = "Bot Description",
        description = "Bot description",
        defaultValue = StringUtils.EMPTY,
        type = AttributeType.STRING
    )
    String description() default StringUtils.EMPTY;

    /**
     * {@link TGBot} short description
     *
     * @return {@link TGBot} short description
     */
    @AttributeDefinition(
        name = "Bot Short Description",
        description = "Bot short description",
        defaultValue = StringUtils.EMPTY,
        type = AttributeType.STRING
    )
    String short$_$description() default StringUtils.EMPTY;

    /**
     * {@link JCRPath} where persistent data related to the {@link TGBot} is stored
     *
     * @return JCR path where persistent data related to the {@link TGBot} is stored
     */
    @AttributeDefinition(
        name = "JCR Home",
        description = "JCR path where persistent data related to this bot is stored",
        defaultValue = StringUtils.EMPTY,
        type = AttributeType.STRING
    )
    String jcr_home() default StringUtils.EMPTY;

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
