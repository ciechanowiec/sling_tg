package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;
import javax.jcr.Node;
import javax.jcr.Property;

/**
 * Participant in an action or process in Telegram.
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public interface TGActor extends StagedNode<TGActor> {

    /**
     * Name of a {@link Node} where a {@link TGActor} is persisted.
     */
    String ACTOR_NODE_NAME = "actor";

    /**
     * Name of a {@link Property} where an ID of a {@link TGActor} is persisted.
     */
    String PN_ACTOR_ID = "actorID";

    /**
     * Name of a {@link Property} where a first name of a {@link TGActor} is persisted.
     */
    String PN_ACTOR_FIRST_NAME = "actorFirstName";

    /**
     * Name of a {@link Property} where a last name of a {@link TGActor} is persisted.
     */
    String PN_ACTOR_LAST_NAME = "actorLastName";

    /**
     * Name of a {@link Property} where a username of a {@link TGActor} is persisted.
     */
    String PN_ACTOR_USER_NAME = "actorUserName";

    /**
     * Name of a {@link Property} where a bot status of a {@link TGActor} is persisted.
     */
    String PN_ACTOR_IS_BOT = "actorIsBot";

    /**
     * ID of a {@link TGActor}.
     *
     * @return ID of a {@link TGActor}
     */
    long id();

    /**
     * First name of a {@link TGActor}.
     *
     * @return first name of a {@link TGActor}
     */
    String firstName();

    /**
     * Last name of a {@link TGActor}.
     *
     * @return last name of a {@link TGActor}
     */
    String lastName();

    /**
     * Username of a {@link TGActor}.
     *
     * @return username of a {@link TGActor}
     */
    String userName();

    /**
     * Checks if a {@link TGActor} is a bot.
     *
     * @return {@code true} if a {@link TGActor} is a bot; {@code false} otherwise.
     */
    boolean isBot();
}
