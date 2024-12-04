package eu.ciechanowiec.sling.telegram.api;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import javax.jcr.Repository;
import java.util.Comparator;
import java.util.List;

/**
 * Collection of {@link TGMessage} instances.
 */
public interface TGMessages {

    /**
     * Arrange strategy for {@link TGMessage} instances.
     */
    @SuppressWarnings("PublicInnerClass")
    enum ArrangeStrategy {

        /**
         * Sorts {@link TGMessage} instances by their {@link TGSendingDate} date in ascending order (earliest first).
         */
        BY_SENDING_DATE_ASC(Comparator.comparing(tgMessage -> tgMessage.tgSendingDate().get())),

        /**
         * Sorts {@link TGMessage} instances by their {@link TGSendingDate} date in descending order (latest first).
         */
        BY_SENDING_DATE_DESC(Comparator.comparing((TGMessage tgMessage) -> tgMessage.tgSendingDate().get()).reversed()),

        /**
         * Retrieves all {@link TGMessage} instances. No sorting either filtering is applied.
         */
        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        ALL((tgMessage1, tgMessage2) -> NumberUtils.INTEGER_ZERO);

        @Getter
        private final Comparator<TGMessage> comparator;

        ArrangeStrategy(Comparator<TGMessage> comparator) {
            this.comparator = comparator;
        }
    }

    /**
     * Persists in the {@link Repository} and stores in
     * the collection represented by this {@link TGMessages} a new {@link TGMessage}.
     * @param tgMessageToPersist {@link TGMessage} that should be persisted and stored
     * @return persisted {@link TGMessage}
     */
    default TGMessage persistNew(TGMessage tgMessageToPersist) {
        return persistNew(tgMessageToPersist, true);
    }

    /**
     * Persists in the {@link Repository} and stores in
     * the collection represented by this {@link TGMessages} a new {@link TGMessage}.
     * @param tgMessageToPersist {@link TGMessage} that should be persisted and stored
     * @param doPersistBinaries if {@code true}, the binaries from the passed {@link TGMessage} will be persisted in
     *                          the {@link Repository}; if {@code false}, the binaries will not be persisted and
     *                          the passed {@link TGMessage} will be persisted without them
     * @return persisted {@link TGMessage}
     */
    TGMessage persistNew(TGMessage tgMessageToPersist, boolean doPersistBinaries);

    /**
     * Retrieves all {@link TGMessage} instances from this {@link TGMessages} instance.
     * Messages are arranged with {@link ArrangeStrategy#BY_SENDING_DATE_ASC}.
     * @return all {@link TGMessage} instances from this {@link TGMessages} instance
     *         arranged with {@link ArrangeStrategy#BY_SENDING_DATE_ASC}
     */
    List<TGMessage> all();

    /**
     * Retrieves all {@link TGMessage} instances from this {@link TGMessages} instance
     * according to the specified {@link ArrangeStrategy}.
     * @param arrangeStrategy {@link ArrangeStrategy} to be applied for retrieving {@link TGMessage} instances
     * @return all {@link TGMessage} instances from this {@link TGMessages} instance
     *         retrieved according to the specified {@link ArrangeStrategy}
     */
    List<TGMessage> all(ArrangeStrategy arrangeStrategy);

    /**
     * Retrieves all active {@link TGMessage} instances from this {@link TGMessages} instance.
     * An active {@link TGMessage} is a {@link TGMessage} whose {@link TGMessage#tgActivationStatus()}
     * returns {@code true} when {@link TGActivationStatus#isActive()} is called.
     * Messages are arranged with {@link ArrangeStrategy#BY_SENDING_DATE_ASC}.
     * @return all {@link TGMessage} instances from this {@link TGMessages} instance
     *         arranged with {@link ArrangeStrategy#BY_SENDING_DATE_ASC}
     */
    List<TGMessage> active();

    /**
     * Deactivates all {@link TGMessage} instances from this {@link TGMessages} instance.
     */
    void deactivateAll();
}
