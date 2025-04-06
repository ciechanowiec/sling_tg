package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.DefaultProperties;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGSendingDate;
import eu.ciechanowiec.sling.telegram.api.WithOriginalUpdate;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@ToString
class TGSendingDateBasic implements TGSendingDate {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<Calendar> sendingDateSupplier;

    TGSendingDateBasic(WithOriginalUpdate withOriginalUpdate, ResourceAccess resourceAccess) {
        sendingDateSupplier = () -> extractSendingDateFromMessage(withOriginalUpdate);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    TGSendingDateBasic(JCRPath pathToNodeWithDateProperty, ResourceAccess resourceAccess) {
        NodeProperties nodeProperties = new NodeProperties(pathToNodeWithDateProperty, resourceAccess);
        sendingDateSupplier = () -> nodeProperties.propertyValue(PN_SENDING_DATE, DefaultProperties.DATE_UNIX_EPOCH);
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    private Calendar extractSendingDateFromMessage(WithOriginalUpdate withOriginalUpdate) {
        Update update = withOriginalUpdate.originalUpdate();
        return Optional.ofNullable(update.getMessage())
            .map(Message::getDate)
            .map(Instant::ofEpochSecond)
            .map(Date::from)
            .map(DateUtils::toCalendar)
            .orElseGet(() -> {
                log.warn(
                    "Unable to get the sending date from this update, "
                        + "so the current one will be used: {}", update
                );
                Instant now = Instant.now();
                Date dateNow = Date.from(now);
                return DateUtils.toCalendar(dateNow);
            });
    }

    @Override
    public TGSendingDate save(ParentJCRPath nodeJCRPath) {
        log.trace("Saving {} at {}", this, nodeJCRPath);
        NodeProperties nodeProperties = new NodeProperties(nodeJCRPath, resourceAccess);
        boolean containsProperty = nodeProperties.containsProperty(PN_SENDING_DATE);
        if (containsProperty) {
            String message = String.format(
                "The node %s already contains the property '%s'", nodeJCRPath, PN_SENDING_DATE
            );
            throw new OccupiedJCRPathException(message);
        }
        nodeProperties.setProperty(PN_SENDING_DATE, get());
        return new TGSendingDateBasic(nodeJCRPath, resourceAccess);
    }

    @Override
    public Calendar get() {
        return sendingDateSupplier.get();
    }
}
