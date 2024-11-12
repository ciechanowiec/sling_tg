package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.*;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@ToString
@SuppressWarnings({
        "ClassWithTooManyFields", "OverlyCoupledClass", "ClassFanOutComplexity",
        "MultipleStringLiterals", "PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals"
})
class TGMessageBasic implements TGMessage {

    @ToString.Exclude
    private final ResourceAccess resourceAccess;
    @ToString.Exclude
    private final Supplier<TGAssets<TGDocument>> tgDocumentsSupplier;
    @ToString.Exclude
    private final Supplier<TGAssets<TGPhoto>> tgPhotosSupplier;
    @ToString.Exclude
    private final Supplier<TGActor> tgActorSupplier;
    @ToString.Exclude
    private final Supplier<TGText> tgTextSupplier;
    @ToString.Exclude
    private final Supplier<TGMessageID> tgMessageIDSupplier;
    @ToString.Exclude
    private final Supplier<TGSendingDate> tgSendingDateSupplier;
    @ToString.Exclude
    private final Supplier<TGActivationStatus> tgActivationStatusSupplier;
    @ToString.Exclude
    private final Supplier<TGCommand> tgCommandSupplier;
    @ToString.Exclude
    private final Supplier<TGAssets<TGVideo>> tgVideosSupplier;
    @ToString.Exclude
    private final Supplier<TGAssets<TGAudio>> tgAudiosSupplier;

    @SuppressWarnings("OverlyCoupledMethod")
    TGMessageBasic(WithOriginalUpdate withOriginalUpdate, TGBot tgBot, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.tgDocumentsSupplier = () -> new TGDocumentsBasic(withOriginalUpdate, tgBot, resourceAccess);
        this.tgPhotosSupplier = () -> new TGPhotosBasic(withOriginalUpdate, tgBot, resourceAccess);
        this.tgActorSupplier = () -> new TGActorBasic(withOriginalUpdate, resourceAccess);
        this.tgTextSupplier = () -> new TGTextBasic(withOriginalUpdate, resourceAccess);
        this.tgMessageIDSupplier = () -> new TGMessageIDBasic(withOriginalUpdate, resourceAccess);
        this.tgSendingDateSupplier = () -> new TGSendingDateBasic(withOriginalUpdate, resourceAccess);
        this.tgActivationStatusSupplier = () -> new TGActivationStatusBasic(true, resourceAccess);
        this.tgCommandSupplier = () -> tgBot.tgCommands().of(tgTextSupplier.get().get(), true);
        this.tgVideosSupplier = () -> new TGVideosBasic(withOriginalUpdate, tgBot, resourceAccess);
        this.tgAudiosSupplier = () -> new TGAudiosBasic(withOriginalUpdate, tgBot, resourceAccess);
        log.trace("Initialized {}", this);
    }

    @SuppressWarnings("VariableDeclarationUsageDistance")
    TGMessageBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        JCRPath tgDocumentsJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGAssets.DOCUMENTS_NODE_NAME);
        JCRPath tgPhotosJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGAssets.PHOTOS_NODE_NAME);
        JCRPath tgActorJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGActor.ACTOR_NODE_NAME);
        JCRPath tgVideosJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGAssets.VIDEOS_NODE_NAME);
        JCRPath tgAudiosJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGAssets.AUDIOS_NODE_NAME);
        this.tgDocumentsSupplier = () -> new TGDocumentsBasic(tgDocumentsJCRPath, resourceAccess);
        this.tgPhotosSupplier = () -> new TGPhotosBasic(tgPhotosJCRPath, resourceAccess);
        this.tgActorSupplier = () -> new TGActorBasic(tgActorJCRPath, resourceAccess);
        this.tgTextSupplier = () -> new TGTextBasic(jcrPath, resourceAccess);
        this.tgMessageIDSupplier = () -> new TGMessageIDBasic(jcrPath, resourceAccess);
        this.tgSendingDateSupplier = () -> new TGSendingDateBasic(jcrPath, resourceAccess);
        this.tgActivationStatusSupplier = () -> new TGActivationStatusBasic(jcrPath, resourceAccess);
        this.tgCommandSupplier = () -> TGCommandBasic.NONE;
        this.tgVideosSupplier = () -> new TGVideosBasic(tgVideosJCRPath, resourceAccess);
        this.tgAudiosSupplier = () -> new TGAudiosBasic(tgAudiosJCRPath, resourceAccess);
        log.trace("Initialized {}", this);
    }

    TGMessageBasic(TGMessageNoBinariesPersistence tgMessageNoBinariesPersistence, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.tgDocumentsSupplier = tgMessageNoBinariesPersistence::tgDocuments;
        this.tgPhotosSupplier = tgMessageNoBinariesPersistence::tgPhotos;
        this.tgActorSupplier = tgMessageNoBinariesPersistence::tgActor;
        this.tgTextSupplier = tgMessageNoBinariesPersistence::tgText;
        this.tgMessageIDSupplier = tgMessageNoBinariesPersistence::tgMessageID;
        this.tgSendingDateSupplier = tgMessageNoBinariesPersistence::tgSendingDate;
        this.tgActivationStatusSupplier = tgMessageNoBinariesPersistence::tgActivationStatus;
        this.tgCommandSupplier = tgMessageNoBinariesPersistence::tgCommand;
        this.tgVideosSupplier = tgMessageNoBinariesPersistence::tgVideos;
        this.tgAudiosSupplier = tgMessageNoBinariesPersistence::tgAudios;
        log.trace("Initialized {}", this);
    }

    @Override
    public TGAssets<TGDocument> tgDocuments() {
        return tgDocumentsSupplier.get();
    }

    @Override
    public TGAssets<TGPhoto> tgPhotos() {
        return tgPhotosSupplier.get();
    }

    @Override
    public TGAssets<TGVideo> tgVideos() {
        return tgVideosSupplier.get();
    }

    @Override
    public TGAssets<TGAudio> tgAudios() {
        return tgAudiosSupplier.get();
    }

    @Override
    public TGText tgText() {
        return tgTextSupplier.get();
    }

    @Override
    public TGMessageID tgMessageID() {
        return tgMessageIDSupplier.get();
    }

    @Override
    public TGSendingDate tgSendingDate() {
        return tgSendingDateSupplier.get();
    }

    @Override
    public TGActivationStatus tgActivationStatus() {
        return tgActivationStatusSupplier.get();
    }

    @Override
    public TGActor tgActor() {
        return tgActorSupplier.get();
    }

    @Override
    public TGCommand tgCommand() {
        return tgCommandSupplier.get();
    }

    @SneakyThrows
    @Override
    public TGMessage save(TargetJCRPath targetJCRPath) {
        log.trace("Saving {} to {}", this, targetJCRPath);
        targetJCRPath.assertThatJCRPathIsFree(resourceAccess);
        try (ResourceResolver resourceResolver = resourceAccess.acquireAccess()) {
            Resource message = ResourceUtil.getOrCreateResource(
                    resourceResolver, targetJCRPath.get(), Map.of(
                            JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_ORDERED_FOLDER
                    ), null, true
            );
            log.trace("Ensured {}", message);
        }
        TGAssets<TGDocument> savedDocuments = tgDocuments().save(new TargetJCRPath(
                new ParentJCRPath(targetJCRPath), TGAssets.DOCUMENTS_NODE_NAME
        ));
        log.trace("Saved {} for the message at {}", savedDocuments, targetJCRPath);
        TGAssets<TGPhoto> savedPhotos = tgPhotos().save(new TargetJCRPath(
                new ParentJCRPath(targetJCRPath), TGAssets.PHOTOS_NODE_NAME
        ));
        log.trace("Saved {} for the message at {}", savedPhotos, targetJCRPath);
        TGActor savedActor = tgActor().save(new TargetJCRPath(
                new ParentJCRPath(targetJCRPath), TGActor.ACTOR_NODE_NAME
        ));
        log.trace("Saved {} for the message at {}", savedActor, targetJCRPath);
        TGText savedText = tgText().save(new ParentJCRPath(targetJCRPath));
        log.trace("Saved {} for the message at {}", savedText, targetJCRPath);
        TGMessageID savedMessageID = tgMessageID().save(new ParentJCRPath(targetJCRPath));
        log.trace("Saved {} for the message at {}", savedMessageID, targetJCRPath);
        TGSendingDate savedSendingDate = tgSendingDate().save(new ParentJCRPath(targetJCRPath));
        log.trace("Saved {} for the message at {}", savedSendingDate, targetJCRPath);
        TGActivationStatus savedTGActivationStatus = tgActivationStatus().save(new ParentJCRPath(targetJCRPath));
        log.trace("Saved {} for the message at {}", savedTGActivationStatus, targetJCRPath);
        TGAssets<TGVideo> savedVideos = tgVideos().save(new TargetJCRPath(
                new ParentJCRPath(targetJCRPath), TGAssets.VIDEOS_NODE_NAME
        ));
        log.trace("Saved {} for the message at {}", savedVideos, targetJCRPath);
        TGAssets<TGAudio> savedAudios = tgAudios().save(new TargetJCRPath(
                new ParentJCRPath(targetJCRPath), TGAssets.AUDIOS_NODE_NAME
        ));
        log.trace("Saved {} for the message at {}", savedAudios, targetJCRPath);
        return new TGMessageBasic(targetJCRPath, resourceAccess);
    }
}
