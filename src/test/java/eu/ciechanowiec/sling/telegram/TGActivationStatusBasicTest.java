package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.rocket.test.TestEnvironment;
import eu.ciechanowiec.sling.telegram.api.TGActivationStatus;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TGActivationStatusBasicTest extends TestEnvironment {

    TGActivationStatusBasicTest() {
        super(ResourceResolverType.JCR_OAK);
    }

    @Test
    @SuppressWarnings("squid:S5778")
    void mustSave() {
        TGActivationStatus inMemoryStatusActivated = new TGActivationStatusBasic(true, fullResourceAccess);
        assertTrue(inMemoryStatusActivated.isActive());
        TGActivationStatus inMemoryStatusDeactivated = inMemoryStatusActivated.deactivate();
        assertFalse(inMemoryStatusDeactivated.isActive());
        TGActivationStatus inMemoryStatusReactivated = inMemoryStatusDeactivated.activate();
        assertTrue(inMemoryStatusReactivated.isActive());
        String rawPath = "/content/sling/telegram/activation-status";
        context.build().resource(rawPath).commit();
        ParentJCRPath nodeJCRPath = new ParentJCRPath(new TargetJCRPath(rawPath));
        TGActivationStatus persistedStatus = inMemoryStatusReactivated.save(nodeJCRPath);
        assertAll(
                () -> assertTrue(persistedStatus.isActive()),
                () -> assertTrue(new TGActivationStatusBasic(nodeJCRPath, fullResourceAccess).isActive())
        );
        assertThrows(
                OccupiedJCRPathException.class,
                () -> new TGActivationStatusBasic(nodeJCRPath, fullResourceAccess).save(nodeJCRPath)
        );
    }
}
