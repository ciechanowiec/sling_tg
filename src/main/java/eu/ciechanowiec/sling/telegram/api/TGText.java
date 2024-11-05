package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedProperty;

import javax.jcr.Property;

/**
 * Text in Telegram.
 */
public interface TGText extends StagedProperty<TGText> {

    /**
     * Name of a {@link Property} where a {@link TGText} is persisted.
     */
    String PN_TEXT = "text";

    /**
     * Text in Telegram.
     * @return text in Telegram
     */
    String get();
}
