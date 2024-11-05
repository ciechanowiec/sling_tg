package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.commons.UnwrappedIteration;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGActivationStatus;
import eu.ciechanowiec.sling.telegram.api.TGMessage;
import eu.ciechanowiec.sling.telegram.api.TGMessages;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ToString
class TGMessagesBasic implements TGMessages {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    private final JCRPath jcrPath;

    TGMessagesBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.jcrPath = jcrPath;
        log.trace("Initialized {}", this);
    }

    @Override
    public TGMessage persistNew(TGMessage tgMessageToPersist) {
        log.trace("Persisting {} for {}", tgMessageToPersist, this);
        TargetJCRPath newMessagePath = new TargetJCRPath(new ParentJCRPath(jcrPath), UUID.randomUUID());
        return tgMessageToPersist.save(newMessagePath);
    }

    @Override
    public List<TGMessage> all() {
        log.trace("Retrieving all messages for {}", this);
        return all(ArrangeStrategy.BY_SENDING_DATE_ASC);
    }

    @Override
    public List<TGMessage> all(ArrangeStrategy arrangeStrategy) {
        log.trace("Retrieving all messages for {} with {}", this, arrangeStrategy);
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            String jcrPathRaw = jcrPath.get();
            return Optional.ofNullable(resourceResolver.getResource(jcrPathRaw))
                    .map(Resource::listChildren)
                    .map(UnwrappedIteration::new)
                    .stream()
                    .flatMap(UnwrappedIteration::stream)
                    .map(Resource::getPath)
                    .map(TargetJCRPath::new)
                    .<TGMessage>map(targetJCRPath -> new TGMessageBasic(targetJCRPath, resourceAccess))
                    .sorted(arrangeStrategy.comparator())
                    .toList();
        }
    }

    @Override
    public void deactivateAll() {
        log.trace("Deactivating all messages for {}", this);
        all().stream().map(TGMessage::tgActivationStatus).forEach(TGActivationStatus::deactivate);
    }
}
