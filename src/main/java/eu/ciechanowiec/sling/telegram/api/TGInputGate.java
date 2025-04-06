package eu.ciechanowiec.sling.telegram.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Input gate to which all {@link Update} instances for a given {@link TGBot} are pushed.
 */
public interface TGInputGate extends LongPollingUpdateConsumer {

    /**
     * Sequentially passes all specified {@link Update} instances to asynchronous consumption by
     * {@link TGInputGate#consume(Update)}.
     *
     * @param updates {@link List} of {@link Update} instances to be consumed
     * @return {@link List} of {@link CompletableFuture} instances where every instance represents a
     * {@link CompletableFuture} used to asynchronously consume a single {@link Update} instance
     */
    List<CompletableFuture<Void>> consumeAsync(List<Update> updates);

    /**
     * Passes the specified {@link Update} instance to asynchronous consumption by {@link TGInputGate#consume(Update)}.
     *
     * @param update {@link Update} instance to be consumed
     * @return {@link CompletableFuture} instance used to asynchronously consume the specified {@link Update} instance
     */
    CompletableFuture<Void> consumeAsync(Update update);

    /**
     * Passes the specified {@link Update} instance wrapped into a {@link TGUpdate} to
     * {@link TGRootUpdatesReceiver#receive(TGUpdate)}.
     *
     * @param update {@link Update} instance to be consumed
     */
    void consume(Update update);
}
