package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.ConditionalAsset;
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
import java.util.Optional;
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
    private final Supplier<Optional<TGDocument>> tgDocumentSupplier;
    @ToString.Exclude
    private final Supplier<Optional<TGVideo>> tgVideoSupplier;
    @ToString.Exclude
    private final Supplier<Optional<TGAudio>> tgAudioSupplier;
    @ToString.Exclude
    private final Supplier<Optional<TGVoice>> tgVoiceSupplier;

    TGMessageBasic(WithOriginalUpdate withOriginalUpdate, TGBot tgBot, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        this.tgPhotosSupplier = () -> new TGPhotosBasic(withOriginalUpdate, tgBot, resourceAccess);
        this.tgActorSupplier = () -> new TGActorBasic(withOriginalUpdate, resourceAccess);
        this.tgTextSupplier = () -> new TGTextBasic(withOriginalUpdate, resourceAccess);
        this.tgMessageIDSupplier = () -> new TGMessageIDBasic(withOriginalUpdate, resourceAccess);
        this.tgSendingDateSupplier = () -> new TGSendingDateBasic(withOriginalUpdate, resourceAccess);
        this.tgActivationStatusSupplier = () -> new TGActivationStatusBasic(true, resourceAccess);
        this.tgCommandSupplier = () -> tgBot.tgCommands().of(tgTextSupplier.get().get(), true);
        this.tgDocumentSupplier = () -> Optional.ofNullable(withOriginalUpdate.originalUpdate().getMessage())
                .flatMap(message -> Optional.ofNullable(message.getDocument()))
                .map(WithOriginalMetadata::new)
                .map(withOriginalMetadata -> new TGAssetBasic(withOriginalMetadata, tgBot));
        this.tgVideoSupplier = () -> Optional.ofNullable(withOriginalUpdate.originalUpdate().getMessage())
                .flatMap(message -> Optional.ofNullable(message.getVideo()))
                .map(WithOriginalMetadata::new)
                .map(withOriginalMetadata -> new TGAssetBasic(withOriginalMetadata, tgBot));
        this.tgAudioSupplier = () -> Optional.ofNullable(withOriginalUpdate.originalUpdate().getMessage())
                .flatMap(message -> Optional.ofNullable(message.getAudio()))
                .map(WithOriginalMetadata::new)
                .map(withOriginalMetadata -> new TGAssetBasic(withOriginalMetadata, tgBot));
        this.tgVoiceSupplier = () -> Optional.ofNullable(withOriginalUpdate.originalUpdate().getMessage())
                .flatMap(message -> Optional.ofNullable(message.getVoice()))
                .map(WithOriginalMetadata::new)
                .map(withOriginalMetadata -> new TGAssetBasic(withOriginalMetadata, tgBot));
        log.trace("Initialized {}", this);
    }

    @SuppressWarnings("VariableDeclarationUsageDistance")
    TGMessageBasic(JCRPath jcrPath, ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        JCRPath tgPhotosJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGAssets.PHOTOS_NODE_NAME);
        JCRPath tgActorJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGActor.ACTOR_NODE_NAME);
        JCRPath tgDocumentJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGDocument.DOCUMENT_NODE_NAME);
        JCRPath tgVideoJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGVideo.VIDEO_NODE_NAME);
        JCRPath tgAudioJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGAudio.AUDIO_NODE_NAME);
        JCRPath tgVoiceJCRPath = new TargetJCRPath(new ParentJCRPath(jcrPath), TGVoice.VOICE_NODE_NAME);
        this.tgPhotosSupplier = () -> new TGPhotosBasic(tgPhotosJCRPath, resourceAccess);
        this.tgActorSupplier = () -> new TGActorBasic(tgActorJCRPath, resourceAccess);
        this.tgTextSupplier = () -> new TGTextBasic(jcrPath, resourceAccess);
        this.tgMessageIDSupplier = () -> new TGMessageIDBasic(jcrPath, resourceAccess);
        this.tgSendingDateSupplier = () -> new TGSendingDateBasic(jcrPath, resourceAccess);
        this.tgActivationStatusSupplier = () -> new TGActivationStatusBasic(jcrPath, resourceAccess);
        this.tgCommandSupplier = () -> TGCommandBasic.NONE;
        this.tgDocumentSupplier = () -> new ConditionalAsset(
                tgDocumentJCRPath, resourceAccess
        ).get().map(TGAssetBasic::new);
        this.tgVideoSupplier = () -> new ConditionalAsset(tgVideoJCRPath, resourceAccess).get().map(TGAssetBasic::new);
        this.tgAudioSupplier = () -> new ConditionalAsset(tgAudioJCRPath, resourceAccess).get().map(TGAssetBasic::new);
        this.tgVoiceSupplier = () -> new ConditionalAsset(tgVoiceJCRPath, resourceAccess).get().map(TGAssetBasic::new);
        log.trace("Initialized {}", this);
    }

    @Override
    public TGAssets<TGPhoto> tgPhotos() {
        return tgPhotosSupplier.get();
    }

    @Override
    public Optional<TGDocument> tgDocument() {
        return tgDocumentSupplier.get();
    }

    @Override
    public Optional<TGVideo> tgVideo() {
        return tgVideoSupplier.get();
    }

    @Override
    public Optional<TGAudio> tgAudio() {
        return tgAudioSupplier.get();
    }

    @Override
    public Optional<TGVoice> tgVoice() {
        return tgVoiceSupplier.get();
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
        tgDocument().map(tgDocument -> new SaveTGAsset(tgDocument, resourceAccess)).flatMap(
                saveTGAsset -> saveTGAsset.save(
                        new TargetJCRPath(new ParentJCRPath(targetJCRPath), TGDocument.DOCUMENT_NODE_NAME)
                )
        ).ifPresent(tgAsset -> log.trace("Saved {} for the message at {}", tgAsset, targetJCRPath));
        tgVideo().map(tgVideo -> new SaveTGAsset(tgVideo, resourceAccess)).flatMap(
                saveTGAsset -> saveTGAsset.save(
                        new TargetJCRPath(new ParentJCRPath(targetJCRPath), TGVideo.VIDEO_NODE_NAME)
                )
        ).ifPresent(tgAsset -> log.trace("Saved {} for the message at {}", tgAsset, targetJCRPath));
        tgAudio().map(tgAudio -> new SaveTGAsset(tgAudio, resourceAccess)).flatMap(
                saveTGAsset -> saveTGAsset.save(
                        new TargetJCRPath(new ParentJCRPath(targetJCRPath), TGAudio.AUDIO_NODE_NAME)
                )
        ).ifPresent(tgAsset -> log.trace("Saved {} for the message at {}", tgAsset, targetJCRPath));
        tgVoice().map(tgVoice -> new SaveTGAsset(tgVoice, resourceAccess)).flatMap(
                saveTGAsset -> saveTGAsset.save(
                        new TargetJCRPath(new ParentJCRPath(targetJCRPath), TGVoice.VOICE_NODE_NAME)
                )
        ).ifPresent(tgAsset -> log.trace("Saved {} for the message at {}", tgAsset, targetJCRPath));
        return new TGMessageBasic(targetJCRPath, resourceAccess);
    }
}
