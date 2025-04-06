package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.DefaultProperties;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGMessageID;
import eu.ciechanowiec.sling.telegram.api.WithOriginalUpdate;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@ToString
class TGMessageIDBasic implements TGMessageID {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<Integer> messageIDSupplier;

    TGMessageIDBasic(WithOriginalUpdate withOriginalUpdate, ResourceAccess resourceAccess) {
        messageIDSupplier = () -> extractMessageIDFromMessage(withOriginalUpdate);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    @SuppressWarnings("PMD.LongVariable")
    TGMessageIDBasic(JCRPath pathToNodeWithMessageIDProperty, ResourceAccess resourceAccess) {
        NodeProperties nodeProperties = new NodeProperties(pathToNodeWithMessageIDProperty, resourceAccess);
        messageIDSupplier = () -> nodeProperties.propertyValue(
            PN_MESSAGE_ID, DefaultProperties.of(-1)
        ).intValue();
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    private int extractMessageIDFromMessage(WithOriginalUpdate withOriginalUpdate) {
        Update update = withOriginalUpdate.originalUpdate();
        return Optional.ofNullable(update.getMessage())
            .map(Message::getMessageId)
            .orElseGet(
                () -> {
                    log.debug("Unable to extract message ID from this Update, so '-1' will be returned: {}", update);
                    return -1;
                }
            );
    }

    @Override
    public TGMessageID save(ParentJCRPath nodeJCRPath) {
        log.trace("Saving {} at {}", this, nodeJCRPath);
        NodeProperties nodeProperties = new NodeProperties(nodeJCRPath, resourceAccess);
        boolean containsProperty = nodeProperties.containsProperty(PN_MESSAGE_ID);
        if (containsProperty) {
            String message = String.format(
                "The node %s already contains the property '%s'", nodeJCRPath, PN_MESSAGE_ID
            );
            throw new OccupiedJCRPathException(message);
        }
        nodeProperties.setProperty(PN_MESSAGE_ID, DefaultProperties.of(asLong()));
        return new TGMessageIDBasic(nodeJCRPath, resourceAccess);
    }

    @Override
    public String asString() {
        return String.valueOf(asInt());
    }

    @Override
    public long asLong() {
        return asInt();
    }

    @Override
    public int asInt() {
        return messageIDSupplier.get();
    }
}
