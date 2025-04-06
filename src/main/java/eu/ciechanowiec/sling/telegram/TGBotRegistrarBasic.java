package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.FullResourceAccess;
import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.telegram.api.TGBot;
import eu.ciechanowiec.sling.telegram.api.TGBotRegistrar;
import eu.ciechanowiec.sling.telegram.api.TGBotRegistration;
import eu.ciechanowiec.sling.telegram.api.TGBotToken;
import eu.ciechanowiec.sling.telegram.api.TGIOGate;
import eu.ciechanowiec.sling.telegram.api.TGInputGate;
import eu.ciechanowiec.sling.telegram.api.TGOutputGate;
import eu.ciechanowiec.sling.telegram.api.TGRootUpdatesReceiver;
import java.util.Optional;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.DefaultGetUpdatesGenerator;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Basic implementation of {@link TGBotRegistrar}.
 */
@Component(
    service = {TGBotRegistrar.class, TGBotRegistrarBasic.class},
    immediate = true
)
@Slf4j
@ToString
@ServiceDescription("Basic implementation of TGBotRegistrar")
public class TGBotRegistrarBasic implements TGBotRegistrar {

    private final TelegramBotsLongPollingApplication tgBotsApplication;
    private final TGRootUpdatesReceiver tgRootUpdatesReceiver;
    @ToString.Exclude
    private final FullResourceAccess fullResourceAccess;

    /**
     * Constructs an instance of this class.
     *
     * @param tgRootUpdatesReceiver {@link TGRootUpdatesReceiver} that will receive {@link Update}-s from Telegram as
     *                              first
     * @param fullResourceAccess    {@link ResourceAccess} that will be used to acquire access to resources
     */
    @Activate
    public TGBotRegistrarBasic(
        @Reference(cardinality = ReferenceCardinality.MANDATORY)
        TGRootUpdatesReceiver tgRootUpdatesReceiver,
        @Reference(cardinality = ReferenceCardinality.MANDATORY)
        FullResourceAccess fullResourceAccess
    ) {
        this.tgRootUpdatesReceiver = tgRootUpdatesReceiver;
        this.tgBotsApplication = new TelegramBotsLongPollingApplication();
        this.fullResourceAccess = fullResourceAccess;
        log.info("Initialized {}", this);
    }

    @Override
    public Optional<TGBotRegistration> registerBot(TGBot tgBot) {
        log.debug("Registering {}", tgBot);
        TGBotToken tgBotToken = tgBot.tgBotToken();
        String botTokenValue = tgBotToken.get();
        TGOutputGate tgOutputGate = new TGOutputGateBasic(tgBotToken);
        TGInputGate tgInputGate = new TGInputGateBasic(tgRootUpdatesReceiver, tgBot, fullResourceAccess);
        try {
            BotSession botSession = tgBotsApplication.registerBot(
                botTokenValue, tgBot::telegramUrl, new DefaultGetUpdatesGenerator(), tgInputGate
            );
            boolean isSessionRunning = botSession.isRunning();
            log.info("Registered {}. Is tgBot session running: {}", tgBot, isSessionRunning);
            return Optional.of(
                new TGBotRegistration() {

                    @Override
                    public TGIOGate tgIOGate() {
                        return new TGIOGateBasic(tgInputGate, tgOutputGate);
                    }

                    @Override
                    public BotSession botSession() {
                        return botSession;
                    }
                }
            );
        } catch (TelegramApiException exception) {
            String message = "Unable to register %s".formatted(tgBot);
            log.error(message, tgBot, exception);
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("PMD.CloseResource")
    public boolean unregisterBot(TGBot tgBot) throws TelegramApiException {
        log.debug("Unregistering {}", tgBot);
        String tgBotTokenValue = tgBot.tgBotToken().get();
        tgBotsApplication.unregisterBot(tgBotTokenValue);
        @SuppressWarnings("resource")
        BotSession botSession = tgBot.botSession();
        boolean isSessionRunning = botSession.isRunning();
        log.info("Unregistered {}. Is bot session running: {}", tgBot, isSessionRunning);
        return !isSessionRunning;
    }

    @Deactivate
    @SuppressWarnings({"IllegalCatch", "PMD.AvoidCatchingGenericException"})
    void deactivate() {
        log.info("Deactivating {}", this);
        try {
            tgBotsApplication.close();
        } catch (Exception exception) {
            log.error("Unable to close the TG application", exception);
        }
        boolean isRunning = tgBotsApplication.isRunning();
        log.info("Deactivated {}. Is TG application running? Answer: {}", this, isRunning);
    }
}
