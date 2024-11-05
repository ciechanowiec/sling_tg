package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGChat;
import eu.ciechanowiec.sling.telegram.api.TGMessages;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import java.util.Map;

@Slf4j
@ToString
class TGChatBasic implements TGChat {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    private final JCRPath jcrPath;

    TGChatBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.jcrPath = jcrPath;
        log.trace("Initialized {}", this);
    }

    @SneakyThrows
    @Override
    public TGMessages tgMessages() {
        JCRPath messagesJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), MESSAGES_NODE_NAME);
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String messagesJCRPathRaw = messagesJCRPath.get();
            Resource messages = ResourceUtil.getOrCreateResource(
                    resourceResolver, messagesJCRPathRaw,
                    Map.of(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_ORDERED_FOLDER),
                    JcrResourceConstants.NT_SLING_FOLDER, true
            );
            log.trace("Ensured {}", messages);
            return new TGMessagesBasic(messagesJCRPath, resourceAccess);
        }
    }
}
