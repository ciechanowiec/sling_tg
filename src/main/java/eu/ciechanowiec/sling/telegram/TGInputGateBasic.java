package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.telegram.api.TGBot;
import eu.ciechanowiec.sling.telegram.api.TGInputGate;
import eu.ciechanowiec.sling.telegram.api.TGRootUpdatesReceiver;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ToString
class TGInputGateBasic implements TGInputGate {

    private final TGRootUpdatesReceiver tgRootUpdatesReceiver;
    private final TGBot tgBot;
    private final ResourceAccess resourceAccess;

    TGInputGateBasic(TGRootUpdatesReceiver basicTGRootUpdatesReceiver, TGBot tgBot, ResourceAccess resourceAccess) {
        this.tgRootUpdatesReceiver = basicTGRootUpdatesReceiver;
        this.tgBot = tgBot;
        this.resourceAccess = resourceAccess;
        log.info("Initialized {}", this);
    }

    @Override
    public void consume(Update update) {
        log.debug("Received {}", update);
        tgRootUpdatesReceiver.receive(new TGUpdateBasic(update, tgBot, resourceAccess));
    }

    @Override
    public void consume(List<Update> updates) {
        int numOfUpdates = updates.size();
        log.debug("Received {} updates. Will pass them to async execution", numOfUpdates);
        consumeAsync(updates);
    }

    @Override
    public List<CompletableFuture<Void>> consumeAsync(List<Update> updates) {
        int numOfUpdates = updates.size();
        log.debug("Received {} updates. Will pass them to async execution", numOfUpdates);
        return updates.stream()
                      .map(update -> CompletableFuture.runAsync(() -> consume(update)))
                      .toList();
    }

    @Override
    public CompletableFuture<Void> consumeAsync(Update update) {
        log.debug("Received an update. Will pass them to async execution");
        return CompletableFuture.runAsync(() -> consume(update));
    }
}
