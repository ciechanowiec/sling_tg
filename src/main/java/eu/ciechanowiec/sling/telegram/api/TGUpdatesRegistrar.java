package eu.ciechanowiec.sling.telegram.api;

import javax.jcr.Repository;

/**
 * Persists instances of {@link TGUpdate} in the {@link Repository}.
 */
@FunctionalInterface
public interface TGUpdatesRegistrar {

    /**
     * Persists the passed {@link TGUpdate} in the {@link Repository}.
     * @param tgUpdate {@link TGUpdate} that is supposed to be persisted in the {@link Repository}
     * @return {@link TGUpdate} that was persisted in the {@link Repository}
     */
    TGUpdate register(TGUpdate tgUpdate);
}
