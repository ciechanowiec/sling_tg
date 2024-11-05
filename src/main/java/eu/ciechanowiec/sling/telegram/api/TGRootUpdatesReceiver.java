package eu.ciechanowiec.sling.telegram.api;

/**
 * {@link TGUpdatesReceiver} that should be the first among all {@link TGUpdatesReceiver} to receive a {@link TGUpdate}.
 */
@FunctionalInterface
public interface TGRootUpdatesReceiver extends TGUpdatesReceiver {
}
