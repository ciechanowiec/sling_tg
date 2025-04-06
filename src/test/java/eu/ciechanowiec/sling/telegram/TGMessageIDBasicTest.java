package eu.ciechanowiec.sling.telegram;

import static eu.ciechanowiec.sling.telegram.api.TGMessageID.PN_MESSAGE_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.rocket.test.TestEnvironment;
import eu.ciechanowiec.sling.telegram.api.TGMessageID;
import java.util.Map;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TGMessageIDBasicTest extends TestEnvironment {

    TGMessageIDBasicTest() {
        super(ResourceResolverType.JCR_OAK);
    }

    @BeforeEach
    void setup() {
        context.build().resource("/content/message-one", Map.of(PN_MESSAGE_ID, "1234")).commit();
    }

    @Test
    void test() {
        TargetJCRPath messagePathAsTarget = new TargetJCRPath("/content/message-one");
        TGMessageID tgMessageID = new TGMessageIDBasic(messagePathAsTarget, fullResourceAccess);
        ParentJCRPath messagePathAsParent = new ParentJCRPath(messagePathAsTarget);
        assertThrows(
            OccupiedJCRPathException.class,
            () -> tgMessageID.save(messagePathAsParent)
        );
    }
}
