package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.DefaultProperties;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGText;
import eu.ciechanowiec.sling.telegram.api.WithOriginalUpdate;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGTextBasic implements TGText {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<String> textSupplier;

    TGTextBasic(WithOriginalUpdate withOriginalUpdate, ResourceAccess resourceAccess) {
        textSupplier = () -> extractTextFromMessage(withOriginalUpdate);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    TGTextBasic(JCRPath pathToNodeWithTextProperty, ResourceAccess resourceAccess) {
        NodeProperties nodeProperties = new NodeProperties(pathToNodeWithTextProperty, resourceAccess);
        textSupplier = () -> nodeProperties.propertyValue(PN_TEXT, DefaultProperties.STRING_EMPTY);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    private String extractTextFromMessage(WithOriginalUpdate withOriginalUpdate) {
        Update update = withOriginalUpdate.originalUpdate();
        return Optional.ofNullable(update.getMessage())
                .flatMap(message -> Optional.ofNullable(message.getText()))
                .orElseGet(() -> {
                    log.debug(
                            "Unable to find message text in this Update, so the empty text will be returned: {}", update
                    );
                    return StringUtils.EMPTY;
                });
    }

    @Override
    public String get() {
        return textSupplier.get();
    }

    @Override
    public TGText save(ParentJCRPath nodeJCRPath) {
        log.trace("Saving {} at {}", this, nodeJCRPath);
        NodeProperties nodeProperties = new NodeProperties(nodeJCRPath, resourceAccess);
        boolean containsProperty = nodeProperties.containsProperty(PN_TEXT);
        if (containsProperty) {
            String message = String.format("The node %s already contains the property '%s'", nodeJCRPath, PN_TEXT);
            throw new OccupiedJCRPathException(message);
        }
        nodeProperties.setProperty(PN_TEXT, DefaultProperties.of(get()));
        return new TGTextBasic(nodeJCRPath, resourceAccess);
    }
}
