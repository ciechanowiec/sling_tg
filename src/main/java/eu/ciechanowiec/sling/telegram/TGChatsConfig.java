package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.telegram.api.TGChat;
import eu.ciechanowiec.sling.telegram.api.TGChats;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration of {@link TGChats}.
 */
@ObjectClassDefinition
public @interface TGChatsConfig {

    /**
     * Default {@link JCRPath} where all {@link TGChat} instances should be stored.
     */
    @SuppressWarnings("squid:S1075")
    String DEFAULT_JCR_PATH = "/content/telegram/chats";

    /**
     * {@link JCRPath} where all {@link TGChat} instances should be stored.
     * @return {@link JCRPath} where all {@link TGChat} instances should be stored
     */
    @AttributeDefinition(
            name = "JCR Path",
            description = "JCR path where all chats should be stored. "
                        + "If it doesn't exist, it will be initiated by the service",
            defaultValue = DEFAULT_JCR_PATH,
            type = AttributeType.STRING
    )
    String jcr_path() default DEFAULT_JCR_PATH;
}
