package eu.ciechanowiec.sling.telegram.api;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Provides an original {@link Update}.
 */
@FunctionalInterface
public interface WithOriginalUpdate {

    /**
     * Original {@link Update}.
     * @return original {@link Update}
     */
    Update originalUpdate();
}
