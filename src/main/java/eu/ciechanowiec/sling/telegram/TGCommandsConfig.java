package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGCommand;
import eu.ciechanowiec.sling.telegram.api.TGCommands;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration of {@link TGCommands}.
 */
@ObjectClassDefinition
public @interface TGCommandsConfig {

    /**
     * Collection of raw {@link TGCommand} mappings, where every mapping represents a single command.
     * @return collection of raw {@link TGCommand} mappings, where every mapping represents a single command
     */
    @AttributeDefinition(
            name = "TG Command Mappings",
            description = "Collection of raw TG Command mappings, where every mapping represents a single command. "
                        + "Every mapping has the following syntax: <literal>###<description>###<is-listable> "
                        + "(### is a delimiter), e.g.: /start###Starts the bot###true",
            defaultValue = StringUtils.EMPTY,
            type = AttributeType.STRING
    )
    String[] tg$_$commands_mappings() default StringUtils.EMPTY;
}
