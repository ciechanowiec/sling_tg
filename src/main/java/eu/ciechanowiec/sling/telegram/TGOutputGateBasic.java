package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGOutputGate;
import eu.ciechanowiec.sling.telegram.api.WithTGBotToken;
import eu.ciechanowiec.sling.telegram.api.WithTelegramUrl;
import eu.ciechanowiec.sneakyfun.SneakyFunction;
import eu.ciechanowiec.sneakyfun.SneakySupplier;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@ToString
@SuppressWarnings({"MultipleStringLiterals", "PMD.AvoidDuplicateLiterals"})
class TGOutputGateBasic implements TGOutputGate {

    private final TelegramClient telegramClient;

    TGOutputGateBasic(WithTGBotToken withTGBotToken, WithTelegramUrl withTelegramUrl) {
        String tokenValue = withTGBotToken.tgBotToken().get();
        TelegramUrl telegramUrl = withTelegramUrl.telegramUrl();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
        this.telegramClient = new OkHttpTelegramClient(okHttpClient, tokenValue, telegramUrl);
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
        return Stream.of(1, 2)
            .sequential()
            .map(attemptNumber -> execute(getFile, deleteOnExit, attemptNumber))
            .flatMap(Optional::stream)
            .findFirst();
    }

    private Optional<File> execute(GetFile getFile, boolean deleteOnExit, int attemptNumber) {
        log.debug("File will be got: {}. Attempt #{}", getFile, attemptNumber);
        try {
            org.telegram.telegrambots.meta.api.objects.File resolvedFile = telegramClient.execute(getFile);
            log.trace("File for getting resolved: {}", resolvedFile);
            return Optional.ofNullable(resolvedFile.getFilePath())
                .map(
                    SneakyFunction.sneaky(
                        filePath -> {
                            log.trace("File from this path will be gotten: {}", filePath);
                            return new File(filePath);
                        }
                    )
                ).filter(File::exists)
                .or(
                    SneakySupplier.sneaky(
                        () -> {
                            log.trace("File will be downloaded: {}", resolvedFile);
                            return Optional.of(telegramClient.downloadFile(resolvedFile));
                        }
                    )
                ).filter(File::exists)
                .map(
                    fileToReturn -> {
                        log.trace("Got this file: {}", fileToReturn);
                        if (deleteOnExit) {
                            fileToReturn.deleteOnExit();
                        }
                        return fileToReturn;
                    }
                );
        } catch (TelegramApiException exception) {
            String exceptionMessage = String.format("Unable to get file: %s", getFile);
            log.error(exceptionMessage, exception);
            return Optional.empty();
        }
    }
}
