package eu.ciechanowiec.sling.telegram.api;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;

/**
 * Collection of {@link TGCommand} instances.
 */
public interface TGCommands {

    /**
     * Creates a {@link SetMyCommands} composed of all instances from this {@link TGCommands} instance that return
     * {@code true} when called {@link TGCommand#isListable()}.
     *
     * @param locale {@link Locale} that will be used to set the language code of the {@link SetMyCommands}
     * @return {@link Optional} containing {@link SetMyCommands} composed of all instances from this {@link TGCommands}
     * instance that return {@code true} when called {@link TGCommand#isListable()}; empty {@link Optional} is returned
     * if there are no such instances in this {@link TGCommands}
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    Optional<SetMyCommands> setMyCommands(Locale locale);

    /**
     * Retrieves a {@link TGCommand} instance whose {@link TGCommand#literal()} matches the specified searched literal.
     *
     * @param searchedLiteral literal that will be used to search for a {@link TGCommand} instance
     * @param onlyListable    {@code true} if only listable {@link TGCommand} instances should be considered;
     *                        {@code false} otherwise
     * @return {@link TGCommand} instance whose {@link TGCommand#literal()} matches the specified searched literal
     */
    TGCommand of(String searchedLiteral, boolean onlyListable);

    /**
     * Retrieves all {@link TGCommand} instances from this {@link TGCommands} instance.
     *
     * @param onlyListable {@code true} if only listable {@link TGCommand} instances should be considered; {@code false}
     *                     otherwise
     * @return all {@link TGCommand} instances from this {@link TGCommands} instance
     */
    Collection<TGCommand> all(boolean onlyListable);
}
