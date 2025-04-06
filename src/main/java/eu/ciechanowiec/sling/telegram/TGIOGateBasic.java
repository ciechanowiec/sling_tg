package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGIOGate;
import eu.ciechanowiec.sling.telegram.api.TGInputGate;
import eu.ciechanowiec.sling.telegram.api.TGOutputGate;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

class TGIOGateBasic implements TGIOGate {

    private final TGInputGate tgInputGate;
    private final TGOutputGate tgOutputGate;

    TGIOGateBasic(TGInputGate tgInputGate, TGOutputGate tgOutputGate) {
        this.tgInputGate = tgInputGate;
        this.tgOutputGate = tgOutputGate;
    }

    @Override
    @SuppressWarnings("squid:S119")
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method)
        throws TelegramApiRequestException {
        return tgOutputGate.execute(method);
    }

    @Override
    public Message execute(SendDocument sendDocument) {
        return tgOutputGate.execute(sendDocument);
    }

    @Override
    public Message execute(SendPhoto sendPhoto) {
        return tgOutputGate.execute(sendPhoto);
    }

    @Override
    public Message execute(SendVideo sendVideo) {
        return tgOutputGate.execute(sendVideo);
    }

    @Override
    public Message execute(SendAudio sendAudio) {
        return tgOutputGate.execute(sendAudio);
    }

    @Override
    public Message execute(SendVoice sendVoice) {
        return tgOutputGate.execute(sendVoice);
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) {
        return tgOutputGate.execute(sendMediaGroup);
    }

    @Override
    public Optional<File> execute(GetFile getFile, boolean deleteOnExit) {
        return tgOutputGate.execute(getFile, deleteOnExit);
    }

    @Override
    public void consume(Update update) {
        tgInputGate.consume(update);
    }

    @Override
    public void consume(List<Update> updates) {
        tgInputGate.consume(updates);
    }

    @Override
    public List<CompletableFuture<Void>> consumeAsync(List<Update> updates) {
        return tgInputGate.consumeAsync(updates);
    }

    @Override
    public CompletableFuture<Void> consumeAsync(Update update) {
        return tgInputGate.consumeAsync(update);
    }
}
