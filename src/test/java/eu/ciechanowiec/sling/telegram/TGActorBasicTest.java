package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;
import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.rocket.test.TestEnvironment;
import eu.ciechanowiec.sling.telegram.api.TGActor;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TGActorBasicTest extends TestEnvironment {

    TGActorBasicTest() {
        super(ResourceResolverType.JCR_OAK);
    }

    @Test
    void mustSave() {
        TargetJCRPath jcrPath = new TargetJCRPath("/content/actors/some-actor");
        StagedNode<TGActor> tgActorToSave = new TGActorBasic(jcrPath, fullResourceAccess);
        TGActor savedActor = tgActorToSave.save(jcrPath);
        TGActor freshTGActor = new TGActorBasic(jcrPath, fullResourceAccess);
        assertAll(
                () -> assertEquals(NumberUtils.LONG_ZERO, savedActor.id()),
                () -> assertEquals(NumberUtils.LONG_ZERO, freshTGActor.id()),
                () -> assertThrows(OccupiedJCRPathException.class, () -> freshTGActor.save(jcrPath))
        );
    }
}
