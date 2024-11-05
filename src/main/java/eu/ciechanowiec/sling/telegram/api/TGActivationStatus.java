package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedProperty;

import javax.jcr.Property;

/**
 * Activation status of a related entity.
 */
public interface TGActivationStatus extends StagedProperty<TGActivationStatus> {

    /**
     * Name of a {@link Property} where a {@link TGActivationStatus} is persisted.
     */
    String PN_IS_ACTIVE = "isActive";

    /**
     * Deactivates a related entity. If the entity is already deactivated, the method is completed without exceptions
     * and the entity remains deactivated.
     * @return {@link TGActivationStatus} of the entity after deactivation is completed.
     */
    TGActivationStatus deactivate();

    /**
     * Activates a related entity. If the entity is already active, the method is completed without exceptions
     * and the entity remains active.
     * @return {@link TGActivationStatus} of the entity after activation is completed.
     */
    TGActivationStatus activate();

    /**
     * Checks if a related entity is active.
     * @return {@code true} if the related entity is active; {@code false} otherwise.
     */
    boolean isActive();
}
