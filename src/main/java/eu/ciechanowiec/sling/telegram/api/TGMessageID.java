package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedProperty;

import javax.jcr.Property;

/**
 * ID that uniquely identifies a {@link TGMessage}.
 */
public interface TGMessageID extends StagedProperty<TGMessageID> {

    /**
     * Name of a {@link Property} where a {@link TGMessageID} is persisted.
     */
    String PN_MESSAGE_ID = "messageID";

    /**
     * ID that uniquely identifies a {@link TGMessage}.
     * @return ID that uniquely identifies a {@link TGMessage}
     */
    String asString();

    /**
     * ID that uniquely identifies a {@link TGMessage}.
     * @return ID that uniquely identifies a {@link TGMessage}
     */
    long asLong();

    /**
     * ID that uniquely identifies a {@link TGMessage}.
     * @return ID that uniquely identifies a {@link TGMessage}
     */
    int asInt();
}
