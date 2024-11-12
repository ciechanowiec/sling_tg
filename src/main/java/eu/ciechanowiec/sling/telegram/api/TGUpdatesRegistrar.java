package eu.ciechanowiec.sling.telegram.api;

import javax.jcr.Repository;

/**
 * Persists instances of {@link TGUpdate} in the {@link Repository}.
 */
public interface TGUpdatesRegistrar {

    /**
     * Persists the passed {@link TGUpdate} in the {@link Repository}.
     * @param tgUpdate {@link TGUpdate} that is supposed to be persisted in the {@link Repository}
     * @return {@link TGUpdate} that was persisted in the {@link Repository}
     */
    TGUpdate register(TGUpdate tgUpdate);

    /**
     * Persists the passed {@link TGUpdate} in the {@link Repository}.
     * @param tgUpdate {@link TGUpdate} that is supposed to be persisted in the {@link Repository}
     * @param doPersistBinaries if {@code true}, the binaries from the passed {@link TGUpdate} will be persisted in
     *                          the {@link Repository}; if {@code false}, the binaries will not be persisted and
     *                          the passed {@link TGUpdate} will be persisted without them
     * @return {@link TGUpdate} that was persisted in the {@link Repository}
     */
    TGUpdate register(TGUpdate tgUpdate, boolean doPersistBinaries);
}
