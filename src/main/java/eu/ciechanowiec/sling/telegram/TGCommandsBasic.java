package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGCommand;
import eu.ciechanowiec.sling.telegram.api.TGCommands;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

/**
 * Basic implementation of {@link TGCommands}.
 */
@Component(
    service = {TGCommands.class, TGCommandsBasic.class},
    immediate = true,
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Slf4j
@ToString
@Designate(ocd = TGCommandsConfig.class)
@ServiceDescription("Basic implementation of TGCommands")
public class TGCommandsBasic implements TGCommands {

    private Collection<TGCommand> tgCommands;

    /**
     * Constructs an instance of this class.
     *
     * @param config configuration of this {@link TGCommands}
     */
    @Activate
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public TGCommandsBasic(TGCommandsConfig config) {
        tgCommands = Set.of();
        configure(config);
    }

    @Modified
    void configure(TGCommandsConfig config) {
        log.info("Configuring {}", this);
        String[] mappings = config.tg$_$commands_mappings();
        tgCommands = Stream.of(mappings)
            .map(mapping -> mapping.split("###"))
            .filter(mappingParts -> mappingParts.length == 3)
            .filter(mappingParts -> mappingParts[2].equals("true") || mappingParts[2].equals("false"))
            .map(mappingParts -> new TGCommandBasic(
                mappingParts[0], mappingParts[1], Boolean.parseBoolean(mappingParts[2]))
            )
            .collect(Collectors.toSet());
        log.info("Configured {}. Raw mappings used: {}", this, Arrays.toString(mappings));
    }

    @Override
    public Optional<SetMyCommands> setMyCommands(Locale locale) {
        List<BotCommand> botCommands = tgCommands.stream()
            .filter(TGCommand::isListable)
            .map(TGCommand::botCommand)
            .toList();
        if (botCommands.isEmpty()) {
            return Optional.empty();
        } else {
            String languageCode = locale.getLanguage();
            return Optional.of(new SetMyCommands(botCommands, new BotCommandScopeDefault(), languageCode));
        }
    }

    @Override
    public TGCommand of(String searchedLiteral, boolean onlyListable) {
        return all(onlyListable).stream()
            .filter(command -> command.literal().equals(searchedLiteral))
            .findFirst()
            .orElse(TGCommandBasic.NONE);
    }

    @Override
    public Collection<TGCommand> all(boolean onlyListable) {
        return tgCommands.stream()
            .filter(command -> !onlyListable || command.isListable())
            .collect(Collectors.toSet());
    }
}
