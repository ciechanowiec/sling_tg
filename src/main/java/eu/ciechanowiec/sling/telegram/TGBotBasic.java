package eu.ciechanowiec.sling.telegram;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.ciechanowiec.sling.telegram.api.TGBot;
import eu.ciechanowiec.sling.telegram.api.TGBotHome;
import eu.ciechanowiec.sling.telegram.api.TGBotID;
import eu.ciechanowiec.sling.telegram.api.TGBotRegistrar;
import eu.ciechanowiec.sling.telegram.api.TGBotRegistration;
import eu.ciechanowiec.sling.telegram.api.TGBotToken;
import eu.ciechanowiec.sling.telegram.api.TGCommands;
import eu.ciechanowiec.sling.telegram.api.TGIOGate;
import eu.ciechanowiec.sling.telegram.api.TGOutputGate;
import eu.ciechanowiec.sneakyfun.SneakyConsumer;
import eu.ciechanowiec.sneakyfun.SneakySupplier;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription;
import org.telegram.telegrambots.meta.api.methods.description.SetMyShortDescription;
import org.telegram.telegrambots.meta.api.methods.name.SetMyName;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

/**
 * Basic implementation of {@link TGBot}.
 */
@Component(
    service = {TGBot.class, TGBotBasic.class},
    immediate = true,
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Slf4j
@ToString
@Designate(
    ocd = TGBotConfig.class,
    factory = true
)
@ServiceDescription("Basic implementation of TGBot")
@SuppressWarnings({"ClassFanOutComplexity", "PMD.ExcessiveImports"})
@SuppressFBWarnings("THROWS_METHOD_THROWS_RUNTIMEEXCEPTION")
public class TGBotBasic implements TGBot {

    private final TGBotRegistrar tgBotRegistrar;
    private final TGCommands tgCommands;
    private TGBotConfigObfuscated config;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @ToString.Exclude
    private TGIOGate tgIOGate;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @ToString.Exclude
    private BotSession botSession;

    /**
     * Constructs an instance of this class.
     *
     * @param tgBotRegistrar {@link TGBotRegistrar} that will be used to register and unregister this {@link TGBot}
     * @param tgCommands     {@link TGCommands} that are serviced by this {@link TGBot}
     * @param config         configuration of this {@link TGBot}
     */
    @Activate
    public TGBotBasic(
        @Reference(cardinality = ReferenceCardinality.MANDATORY)
        TGBotRegistrar tgBotRegistrar,
        @Reference(cardinality = ReferenceCardinality.MANDATORY)
        TGCommands tgCommands,
        TGBotConfig config
    ) {
        this.tgBotRegistrar = tgBotRegistrar;
        this.tgCommands = tgCommands;
        this.config = new TGBotConfigObfuscated(config);
        log.info("Initialized {}", this);
    }

    @Activate
    @Modified
    void configure(TGBotConfig newConfig) {
        log.info("Configuring {}", this);
        this.config = new TGBotConfigObfuscated(newConfig);
        unregisterIfRegistered();
        TGBotRegistration tgBotRegistration = Optional.of(tgBotRegistrar)
            .flatMap(tgbr -> tgbr.registerBot(this))
            .or(
                SneakySupplier.sneaky(
                    () -> {
                        log.warn("Registration of {} failed, will retry", this);
                        TimeUnit.SECONDS.sleep(2);
                        return tgBotRegistrar.registerBot(this);
                    }
                )
            ).orElseThrow();
        this.tgIOGate = tgBotRegistration.tgIOGate();
        this.botSession = tgBotRegistration.botSession();
        setCommands(tgIOGate);
        setName(tgIOGate, config);
        setDescription(tgIOGate, config);
        setShortDescription(tgIOGate, config);
        log.info("Configured {}", this);
    }

    @SuppressWarnings({"IllegalCatch", "PMD.AvoidCatchingGenericException"})
    private void setCommands(TGOutputGate outputGate) {
        log.info("Setting up commands for {}", this);
        String languageCode = Locale.ENGLISH.getLanguage();
        DeleteMyCommands deleteMyCommands = new DeleteMyCommands(new BotCommandScopeDefault(), languageCode);
        try {
            boolean commandsDeleted = outputGate.execute(deleteMyCommands);
            log.info("All old commands deleted: {}. Request: {}. {}", commandsDeleted, deleteMyCommands, this);
            tgCommands.setMyCommands(Locale.ENGLISH)
                .ifPresentOrElse(
                    SneakyConsumer.sneaky(
                        setMyCommands -> {
                            boolean commandsSet = outputGate.execute(setMyCommands);
                            log.info("Commands set: {}. Request: {}. {}", commandsSet, setMyCommands, this);
                        }
                    ),
                    () -> log.info("No commands to set for {}, since there are no commands", this)
                );
        } catch (TelegramApiRequestException exception) { // frequently rather irrelevant exceptions are thrown
            String message = String.format("Unable to set commands for %s", this);
            log.error(message, exception);
        } catch (RuntimeException exception) {
            Optional.of(exception)
                .map(Throwable::getCause)
                .filter(TelegramApiException.class::isInstance)
                .ifPresentOrElse(
                    caused -> {
                        // frequently rather irrelevant exceptions are thrown
                        String message = String.format("Unable to set commands for %s", this);
                        log.error(message, caused);
                    }, () -> {
                        throw exception;
                    }
                );
        }
    }

    private void setName(TGOutputGate outputGate, TGBotConfigObfuscated config) {
        log.info("Setting name for {}", this);
        String name = config.name();
        String languageCode = Locale.ENGLISH.getLanguage();
        SetMyName setMyName = new SetMyName(name, languageCode);
        try {
            boolean nameSet = outputGate.execute(setMyName); // frequently rather irrelevant exceptions are thrown
            log.info("Name set: {}. Request: {}. {}", nameSet, setMyName, this);
        } catch (TelegramApiRequestException exception) {
            String message = String.format("Unable to set name for %s", this);
            log.error(message, exception);
        }
    }

    private void setDescription(TGOutputGate outputGate, TGBotConfigObfuscated config) {
        log.info("Setting description for {}", this);
        String description = config.description();
        String languageCode = Locale.ENGLISH.getLanguage();
        SetMyDescription setMyDescription = new SetMyDescription(description, languageCode);
        try {
            boolean descriptionSet = outputGate.execute(setMyDescription);
            log.info("Description set: {}. Request: {}. {}", descriptionSet, setMyDescription, this);
        } catch (TelegramApiRequestException exception) { // frequently rather irrelevant exceptions are thrown
            String message = String.format("Unable to set description for %s", this);
            log.error(message, exception);
        }
    }

    private void setShortDescription(TGOutputGate outputGate, TGBotConfigObfuscated config) {
        log.info("Setting short description for {}", this);
        String shortDescription = config.shortDescription();
        String languageCode = Locale.ENGLISH.getLanguage();
        SetMyShortDescription setMyDescription = new SetMyShortDescription(shortDescription, languageCode);
        try {
            boolean shortDescriptionSet = outputGate.execute(setMyDescription);
            log.info("Short description set: {}. Request: {}. {}", shortDescriptionSet, setMyDescription, this);
        } catch (TelegramApiRequestException exception) {
            String message = String.format("Unable to set short description for %s", this);
            log.error(message, exception);
        }
    }

    @Deactivate
    void deactivate() {
        log.info("Deactivating {}", this);
        unregisterIfRegistered();
        log.info("Deactivated {}", this);
    }

    @SuppressWarnings("squid:S1166")
    private void unregisterIfRegistered() {
        try {
            log.info("Unregistering if not registered {}", this);
            boolean wasUnregistered = tgBotRegistrar.unregisterBot(this);
            log.info("Unregistered {}. Was successful? Answer: {}", this, wasUnregistered);
        } catch (TelegramApiException exception) {
            String tgResponse = exception.getMessage();
            log.info("{} not registered, hence won't be unregistered. TG response: '{}'", this, tgResponse);
        }
    }

    @Override
    public TGBotHome tgBotHome() {
        return config.tgBotHome();
    }

    @Override
    public TGBotID tgBotID() {
        return config.tgBotID();
    }

    @Override
    public TGBotToken tgBotToken() {
        return config.tgBotToken();
    }

    @Override
    public TGIOGate tgIOGate() {
        return tgIOGate;
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public TGCommands tgCommands() {
        return tgCommands;
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public BotSession botSession() {
        return botSession;
    }

    @Override
    public TelegramUrl telegramUrl() {
        return config.telegramUrl();
    }
}
