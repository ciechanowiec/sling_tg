package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

/**
 * Proxy of a {@link BotCommand}.
 */
public interface TGCommand {

    /**
     * Literal of the "/start" {@link TGCommand}.
     */
    String START_LITERAL = "/start";

    /**
     * Literal of the "/none" {@link TGCommand}.
     */
    String NONE_LITERAL = "/none";

    /**
     * Text of the {@link TGCommand}.
     * @return text of the {@link TGCommand}
     */
    String literal();

    /**
     * Description of the {@link TGCommand}.
     * @return description of the {@link TGCommand}
     */
    String description();

    /**
     * Answers whether this {@link TGCommand} is listable.
     * A {@link TGCommand} is listable when it is supposed to be visible to a non-bot {@link TGActor} in a
     * {@link TGChat} and can be sent by that {@link TGActor}.
     * @return {@code true} if this {@link TGCommand} is listable, {@code false} otherwise
     */
    boolean isListable();

    /**
     * Converts this {@link TGCommand} to a {@link BotCommand}.
     * @return this {@link TGCommand} converted to a {@link BotCommand}
     */
    BotCommand botCommand();

    /**
     * Answers whether the {@link TGCommand#literal()} method of this {@link TGCommand}
     * produces {@link TGCommand#START_LITERAL}.
     * @return {@code true} if the {@link TGCommand#literal()} method of this {@link TGCommand} produces
     *         {@link TGCommand#START_LITERAL}; {@code false} otherwise
     */
    boolean isStart();

    /**
     * Answers whether the {@link TGCommand#literal()} method of this {@link TGCommand}
     * produces {@link TGCommand#NONE_LITERAL}.
     * @return {@code true} if the {@link TGCommand#literal()} method of this {@link TGCommand} produces
     *         {@link TGCommand#NONE_LITERAL}; {@code false} otherwise
     */
    boolean isNone();
}
