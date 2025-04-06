package eu.ciechanowiec.sling.telegram;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.telegram.api.TGBot;
import eu.ciechanowiec.sling.telegram.api.TGChatID;
import eu.ciechanowiec.sling.telegram.api.TGMessage;
import eu.ciechanowiec.sling.telegram.api.TGUpdate;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Basic implementation of {@link TGUpdate}.
 */
@ToString
@Slf4j
public class TGUpdateBasic implements TGUpdate {

    private final Update update;
    @Getter
    private final TGBot tgBot;
    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<TGMessage> tgMessageSupplier;

    /**
     * Constructs an instance of this class.
     *
     * @param update         {@link Update} wrapped by this {@link TGUpdate}
     * @param tgBot          {@link TGBot} that received this {@link TGUpdate}
     * @param resourceAccess {@link ResourceAccess} that will be used to acquire access to resources
     */
    @SuppressWarnings("WeakerAccess")
    public TGUpdateBasic(Update update, TGBot tgBot, ResourceAccess resourceAccess) {
        this.update = update;
        this.tgBot = tgBot;
        this.resourceAccess = resourceAccess;
        tgMessageSupplier = () -> new TGMessageBasic(this, tgBot, resourceAccess);
        log.trace("Initialized {}", this);
    }

    private TGUpdateBasic(Update update, TGBot tgBot, ResourceAccess resourceAccess, TGMessage tgMessage) {
        this.update = update;
        this.tgBot = tgBot;
        this.resourceAccess = resourceAccess;
        this.tgMessageSupplier = () -> tgMessage;
        log.trace("Initialized {}", this);
    }

    @Override
    public TGChatID tgChatID() {
        return Optional.ofNullable(update.getMessage())
            .<TGChatID>map(TGChatIDBasic::new)
            .orElseGet(() -> {
                log.warn(
                    "Unable to find chat ID in this Update, so the default 0L will be returned as chatID: {}",
                    update
                );
                return TGChatIDBasic.UNKNOWN;
            });
    }

    @Override
    public TGUpdate withNewMessage(TGMessage newMessage) {
        return new TGUpdateBasic(update, tgBot, resourceAccess, newMessage);
    }

    @Override
    public TGMessage tgMessage() {
        return tgMessageSupplier.get();
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Update originalUpdate() {
        return update;
    }
}
