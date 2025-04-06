package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.DefaultProperties;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGActivationStatus;
import java.util.function.Supplier;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
class TGActivationStatusBasic implements TGActivationStatus {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    @SuppressWarnings("PMD.LinguisticNaming")
    private final Supplier<Boolean> isActiveSupplier;
    @ToString.Exclude
    private final Supplier<TGActivationStatus> deactivator;
    @ToString.Exclude
    private final Supplier<TGActivationStatus> activator;

    TGActivationStatusBasic(boolean isActive, ResourceAccess resourceAccess) {
        this.isActiveSupplier = () -> isActive;
        this.resourceAccess = resourceAccess;
        this.deactivator = () -> new TGActivationStatusBasic(false, resourceAccess);
        this.activator = () -> new TGActivationStatusBasic(true, resourceAccess);
        log.trace("Initialized {}. Is active: {}", this, isActive);
    }

    @SuppressWarnings("ConstantValue")
    TGActivationStatusBasic(JCRPath pathToNodeWithStatusProperty, ResourceAccess resourceAccess) {
        NodeProperties nodeProperties = new NodeProperties(pathToNodeWithStatusProperty, resourceAccess);
        this.isActiveSupplier = () -> nodeProperties.propertyValue(PN_IS_ACTIVE, DefaultProperties.of(true));
        this.resourceAccess = resourceAccess;
        this.deactivator = () -> {
            nodeProperties.setProperty(PN_IS_ACTIVE, DefaultProperties.of(false));
            return new TGActivationStatusBasic(pathToNodeWithStatusProperty, resourceAccess);
        };
        this.activator = () -> {
            nodeProperties.setProperty(PN_IS_ACTIVE, DefaultProperties.of(true));
            return new TGActivationStatusBasic(pathToNodeWithStatusProperty, resourceAccess);
        };
        log.trace("Initialized {}", this);
    }

    @Override
    public TGActivationStatus deactivate() {
        log.trace("Deactivating {}", this);
        return deactivator.get();
    }

    @Override
    public TGActivationStatus activate() {
        log.trace("Activating {}", this);
        return activator.get();
    }

    @Override
    public boolean isActive() {
        return isActiveSupplier.get();
    }

    @Override
    public TGActivationStatus save(ParentJCRPath nodeJCRPath) {
        log.trace("Saving {} at {}", this, nodeJCRPath);
        NodeProperties nodeProperties = new NodeProperties(nodeJCRPath, resourceAccess);
        boolean containsProperty = nodeProperties.containsProperty(PN_IS_ACTIVE);
        if (containsProperty) {
            String message = String.format(
                "The node %s already contains the property '%s'", nodeJCRPath, PN_IS_ACTIVE
            );
            throw new OccupiedJCRPathException(message);
        }
        nodeProperties.setProperty(PN_IS_ACTIVE, isActive());
        return new TGActivationStatusBasic(nodeJCRPath, resourceAccess);
    }
}
