package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGChat;
import eu.ciechanowiec.sling.telegram.api.TGChats;
import eu.ciechanowiec.sling.telegram.api.WithTGBotID;
import eu.ciechanowiec.sling.telegram.api.WithTGChatID;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Map;

/**
 * Basic implementation of {@link TGChats}.
 */
@Component(
        service = {TGChats.class, TGChatsBasic.class},
        immediate = true,
        configurationPolicy = ConfigurationPolicy.OPTIONAL
)
@Designate(
        ocd = TGChatsConfig.class
)
@Slf4j
@ToString
@ServiceDescription("Basic implementation of TGChats")
public class TGChatsBasic implements TGChats {

    private final ResourceAccess resourceAccess;
    private TGChatsConfig config;

    /**
     * Constructs an instance of this class.
     * @param resourceAccess {@link ResourceAccess} that will be used to acquire access to resources
     * @param config configuration of this {@link TGChats}
     */
    @Activate
    public TGChatsBasic(
            @Reference(cardinality = ReferenceCardinality.MANDATORY) ResourceAccess resourceAccess,
            TGChatsConfig config
    ) {
        this.resourceAccess = resourceAccess;
        this.config = config;
        ensurePath(resourceAccess, new TargetJCRPath(config.jcr_path()));
        log.info("Initialized {}", this);
    }

    @Modified
    void configure(TGChatsConfig config) {
        this.config = config;
        ensurePath(resourceAccess, new TargetJCRPath(config.jcr_path()));
        log.info("Configured {}", this);
    }

    @SneakyThrows
    private void ensurePath(ResourceAccess resourceAccess, JCRPath pathToEnsure) {
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String pathToEnsureRaw = pathToEnsure.get();
            Resource resource = ResourceUtil.getOrCreateResource(
                    resourceResolver, pathToEnsureRaw,
                    Map.of(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_ORDERED_FOLDER), null, true
            );
            log.info("Ensured {}", resource);
        }
    }

    @Override
    @SneakyThrows
    public TGChat getOrCreate(WithTGChatID withTGChatID, WithTGBotID withTGBotID) {
        log.trace("Requested the chat for {} and {}", withTGChatID, withTGBotID);
        String tgBotIDString = withTGBotID.tgBotID().get();
        String tgChatIDString = withTGChatID.tgChatID().asString();
        TargetJCRPath chatsPath = new TargetJCRPath(config.jcr_path());
        TargetJCRPath specificBotChatsPath = new TargetJCRPath(new ParentJCRPath(chatsPath), tgBotIDString);
        JCRPath specificChatPath = new TargetJCRPath(new ParentJCRPath(specificBotChatsPath), tgChatIDString);
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String specificChatPathRaw = specificChatPath.get();
            Resource specificChatResource = ResourceUtil.getOrCreateResource(
                    resourceResolver, specificChatPathRaw,
                    Map.of(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_ORDERED_FOLDER), null, true
            );
            log.trace("Ensured the chat as {}", specificChatResource);
            return new TGChatBasic(specificChatPath, resourceAccess);
        }
    }
}
