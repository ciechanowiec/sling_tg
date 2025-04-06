package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.FullResourceAccess;
import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.telegram.api.TGChat;
import eu.ciechanowiec.sling.telegram.api.TGChats;
import eu.ciechanowiec.sling.telegram.api.TGMessage;
import eu.ciechanowiec.sling.telegram.api.TGMessages;
import eu.ciechanowiec.sling.telegram.api.TGUpdate;
import eu.ciechanowiec.sling.telegram.api.TGUpdatesRegistrar;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.propertytypes.ServiceDescription;

/**
 * Basic implementation of {@link TGUpdatesRegistrar}.
 */
@Component(
    service = {TGUpdatesRegistrar.class, TGUpdatesRegistrarBasic.class},
    immediate = true
)
@ToString
@Slf4j
@ServiceDescription("Basic implementation of TGUpdatesRegistrar")
public class TGUpdatesRegistrarBasic implements TGUpdatesRegistrar {

    private final FullResourceAccess fullResourceAccess;
    private final TGChats tgChats;

    /**
     * Constructs an instance of this class.
     *
     * @param fullResourceAccess {@link ResourceAccess} that will be used to acquire access to resources
     * @param tgChats            {@link TGChats} where {@link TGUpdate}s will be stored
     */
    @Activate
    public TGUpdatesRegistrarBasic(
        @Reference(cardinality = ReferenceCardinality.MANDATORY)
        FullResourceAccess fullResourceAccess,
        @Reference(cardinality = ReferenceCardinality.MANDATORY)
        TGChats tgChats
    ) {
        this.fullResourceAccess = fullResourceAccess;
        this.tgChats = tgChats;
        log.info("Initialized {}", this);
    }

    @Override
    public TGUpdate register(TGUpdate tgUpdate) {
        log.trace("Registering {}", tgUpdate);
        TGMessage tgMessage = tgUpdate.tgMessage();
        TGChat tgChat = tgChats.getOrCreate(tgUpdate, tgUpdate.tgBot());
        log.trace("Retrieved chat: {}", tgChat);
        TGMessages tgMessages = tgChat.tgMessages();
        log.trace("Retrieved messages: {}", tgMessages);
        TGMessage savedTGMessage = tgMessages.persistNew(tgMessage);
        log.debug("Saved: {}", savedTGMessage);
        return tgUpdate.withNewMessage(savedTGMessage);
    }
}
