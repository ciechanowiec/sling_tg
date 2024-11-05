package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedProperty;

import javax.jcr.Property;
import java.util.Calendar;

/**
 * Date when an associated entity was sent.
 */
public interface TGSendingDate extends StagedProperty<TGSendingDate> {

    /**
     * Name of a {@link Property} where a {@link TGSendingDate} is persisted.
     */
    String PN_SENDING_DATE = "sendingDate";

    /**
     * Date when an associated entity was sent.
     * @return date when an associated entity was sent
     */
    Calendar get();
}
