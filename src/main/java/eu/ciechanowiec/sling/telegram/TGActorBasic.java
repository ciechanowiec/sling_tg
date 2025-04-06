package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGActor;
import eu.ciechanowiec.sling.telegram.api.WithOriginalUpdate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@ToString
@Slf4j
class TGActorBasic implements TGActor {

    @ToString.Exclude
    private final Supplier<User> userSupplier;
    @ToString.Exclude
    private final ResourceAccess resourceAccess;

    TGActorBasic(WithOriginalUpdate withOriginalUpdate, ResourceAccess resourceAccess) {
        Update originalUpdate = withOriginalUpdate.originalUpdate();
        this.userSupplier = () -> Optional.ofNullable(originalUpdate.getMessage())
            .flatMap(message -> Optional.ofNullable(message.getFrom()))
            .orElse(
                User.builder()
                    .id(NumberUtils.LONG_ZERO)
                    .firstName(StringUtils.EMPTY)
                    .lastName(StringUtils.EMPTY)
                    .userName(StringUtils.EMPTY)
                    .isBot(false)
                    .build()
            );
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    TGActorBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        NodeProperties nodeProperties = new NodeProperties(jcrPath, resourceAccess);
        this.userSupplier = () -> User.builder()
            .id(nodeProperties.propertyValue(PN_ACTOR_ID, NumberUtils.LONG_ZERO))
            .firstName(nodeProperties.propertyValue(PN_ACTOR_FIRST_NAME, StringUtils.EMPTY))
            .lastName(nodeProperties.propertyValue(PN_ACTOR_LAST_NAME, StringUtils.EMPTY))
            .userName(nodeProperties.propertyValue(PN_ACTOR_USER_NAME, StringUtils.EMPTY))
            .isBot(nodeProperties.propertyValue(PN_ACTOR_IS_BOT, false))
            .build();
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    @Override
    public long id() {
        return userSupplier.get().getId();
    }

    @Override
    public String firstName() {
        return userSupplier.get().getFirstName();
    }

    @Override
    public String lastName() {
        User user = userSupplier.get();
        return Optional.ofNullable(user.getLastName())
            .orElseGet(() -> {
                log.trace("Unable to get the last name of {}. The empty one will be returned", user);
                return StringUtils.EMPTY;
            });
    }

    @Override
    public String userName() {
        User user = userSupplier.get();
        return Optional.ofNullable(user.getUserName())
            .orElseGet(() -> {
                log.trace("Unable to get the user name of {}. The empty one will be returned", user);
                return StringUtils.EMPTY;
            });
    }

    @Override
    public boolean isBot() {
        return userSupplier.get().getIsBot();
    }

    private Map<String, Object> allProps() {
        return Map.of(
            PN_ACTOR_ID, id(),
            PN_ACTOR_FIRST_NAME, firstName(),
            PN_ACTOR_LAST_NAME, lastName(),
            PN_ACTOR_USER_NAME, userName(),
            PN_ACTOR_IS_BOT, isBot()
        );
    }

    @SneakyThrows
    @Override
    public TGActor save(TargetJCRPath targetJCRPath) {
        log.trace("Saving {} to {}", this, targetJCRPath);
        targetJCRPath.assertThatJCRPathIsFree(resourceAccess);
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            Resource resource = ResourceUtil.getOrCreateResource(
                resourceResolver, targetJCRPath.get(), allProps(), null, true
            );
            log.trace("Saved {}", resource);
        }
        return new TGActorBasic(targetJCRPath, resourceAccess);
    }
}
