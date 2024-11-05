package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Output gate through which all external API requests to Telegram for a given {@link TGBot} are executed.
 */
public interface TGOutputGate {

    /**
     * Proxy for {@link TelegramClient#execute(BotApiMethod)}
     * @param method {@link BotApiMethod} instance to be executed
     * @return {@link Serializable} instance returned by the executed {@link BotApiMethod}
     * @param <T> type of the {@link Serializable} instance returned by the executed {@link BotApiMethod}
     * @param <Method> type of the {@link BotApiMethod} instance to be executed
     * @throws TelegramApiRequestException if the execution of the specified {@link BotApiMethod} fails
     */
    @SuppressWarnings("squid:S119")
    <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method)
            throws TelegramApiRequestException;

    /**
     * Proxy for {@link TelegramClient#execute(SendDocument)}
     * @param sendDocument method to be executed
     * @return {@link Message} instance returned by the executed {@link SendDocument}
     */
    Message execute(SendDocument sendDocument);

    /**
     * Proxy for {@link TelegramClient#execute(SendPhoto)}
     * @param sendPhoto {@link SendPhoto} instance to be executed
     * @return {@link Message} instance returned by the executed {@link SendPhoto}
     */
    Message execute(SendPhoto sendPhoto);

    /**
     * Proxy for {@link TelegramClient#execute(SendVideo)}
     * @param sendVideo method to be executed
     * @return {@link Message} instance returned by the executed {@link SendVideo}
     */
    Message execute(SendVideo sendVideo);

    /**
     * Proxy for {@link TelegramClient#execute(SendAudio)}
     * @param sendAudio method to be executed
     * @return {@link Message} instance returned by the executed {@link SendAudio}
     */
    Message execute(SendAudio sendAudio);

    /**
     * Proxy for {@link TelegramClient#execute(SendMediaGroup)}
     * @param sendMediaGroup method to be executed
     * @return list of {@link Message} instances, where every {@link Message} contains
     *         a single element of the sent {@link SendMediaGroup}
     */
    List<Message> execute(SendMediaGroup sendMediaGroup);

    /**
     * Downloads a file specified by the passed {@link GetFile} instance.
     * @param getFile {@link GetFile} instance specifying the file to be downloaded
     * @param deleteOnExit if {@code true}, the downloaded file will be deleted on JVM exit
     * @return {@link Optional} containing the downloaded {@link File} instance if the download was successful,
     *         or an empty {@link Optional} if the download failed
     */
    Optional<File> execute(GetFile getFile, boolean deleteOnExit);
}
