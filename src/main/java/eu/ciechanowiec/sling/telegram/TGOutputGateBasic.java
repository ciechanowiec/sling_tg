package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGBotToken;
import eu.ciechanowiec.sling.telegram.api.TGOutputGate;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Slf4j
@ToString
@SuppressWarnings({"MultipleStringLiterals", "PMD.AvoidDuplicateLiterals"})
class TGOutputGateBasic implements TGOutputGate {

    private final TelegramClient telegramClient;

    TGOutputGateBasic(TGBotToken tgBotToken) {
        String tokenValue = tgBotToken.get();
        this.telegramClient = new OkHttpTelegramClient(tokenValue);
        log.info("Initialized {}", this);
    }

    @SneakyThrows
    @Override
    @SuppressWarnings("squid:S119")
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        log.trace("Executing {}", method);
        return telegramClient.execute(method);
    }

    @SneakyThrows
    @Override
    public Message execute(SendDocument sendDocument) {
        log.trace("Executing {}", sendDocument);
        return telegramClient.execute(sendDocument);
    }

    @SneakyThrows
    @Override
    public Message execute(SendPhoto sendPhoto) {
        log.trace("Executing {}", sendPhoto);
        return telegramClient.execute(sendPhoto);
    }

    @SneakyThrows
    @Override
    public Message execute(SendVideo sendVideo) {
        log.trace("Executing {}", sendVideo);
        return telegramClient.execute(sendVideo);
    }

    @SneakyThrows
    @Override
    public Message execute(SendAudio sendAudio) {
        log.trace("Executing {}", sendAudio);
        return telegramClient.execute(sendAudio);
    }

    @SneakyThrows
    @Override
    public Message execute(SendVoice sendVoice) {
        log.trace("Executing {}", sendVoice);
        return telegramClient.execute(sendVoice);
    }

    @SneakyThrows
    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) {
        log.trace("Executing {}", sendMediaGroup);
        return telegramClient.execute(sendMediaGroup);
    }

    @Override
    public Optional<File> execute(GetFile getFile, boolean deleteOnExit) {
        log.debug("File will be downloaded: {}", getFile);
        try {
            File file = telegramClient.downloadFile(telegramClient.execute(getFile));
            log.trace("File downloaded: {}", file);
            if (deleteOnExit) {
                file.deleteOnExit();
            }
            return Optional.of(file);
        } catch (TelegramApiException exception) {
            String exceptionMessage = String.format("Unable to download file: %s", getFile);
            log.error(exceptionMessage, exception);
            return Optional.empty();
        }
    }
}
