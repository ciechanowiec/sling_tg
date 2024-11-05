package eu.ciechanowiec.sling.telegram.api;

/**
 * Receives instances of a {@link TGUpdate}.
 */
@FunctionalInterface
public interface TGUpdatesReceiver {

    /**
     * Receives a {@link TGUpdate}. The expected execution of this method
     * isn't restricted and is completely defined by the implementation.
     * @param tgUpdate {@link TGUpdate} to receive
     */
    void receive(TGUpdate tgUpdate);
}
