package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.commons.ResourceAccess;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.*;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@SuppressWarnings({
        "ClassFanOutComplexity", "MultipleStringLiterals", "PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals"
})
class TGMessageNoBinariesPersistence implements TGMessage {

    private final ResourceAccess resourceAccess;
    private final TGMessage wrappedTGMessage;

    TGMessageNoBinariesPersistence(TGMessage wrappedTGMessage, ResourceAccess resourceAccess) {
        this.wrappedTGMessage = wrappedTGMessage;
        this.resourceAccess = resourceAccess;
        log.trace("Initialized {}", this);
    }

    @Override
    public TGAssets<TGDocument> tgDocuments() {
        return new TGAssetsNoBinariesPersistence<>(TGDocument.class, wrappedTGMessage.tgDocuments(), resourceAccess);
    }

    @Override
    public TGAssets<TGPhoto> tgPhotos() {
        return new TGAssetsNoBinariesPersistence<>(TGPhoto.class, wrappedTGMessage.tgPhotos(), resourceAccess);
    }

    @Override
    public TGAssets<TGVideo> tgVideos() {
        return new TGAssetsNoBinariesPersistence<>(TGVideo.class, wrappedTGMessage.tgVideos(), resourceAccess);
    }

    @Override
    public TGAssets<TGAudio> tgAudios() {
        return new TGAssetsNoBinariesPersistence<>(TGAudio.class, wrappedTGMessage.tgAudios(), resourceAccess);
    }

    @Override
    public TGText tgText() {
        return wrappedTGMessage.tgText();
    }

    @Override
    public TGMessageID tgMessageID() {
        return wrappedTGMessage.tgMessageID();
    }

    @Override
    public TGSendingDate tgSendingDate() {
        return wrappedTGMessage.tgSendingDate();
    }

    @Override
    public TGActivationStatus tgActivationStatus() {
        return wrappedTGMessage.tgActivationStatus();
    }

    @Override
    public TGActor tgActor() {
        return wrappedTGMessage.tgActor();
    }

    @Override
    public TGCommand tgCommand() {
        return wrappedTGMessage.tgCommand();
    }

    @Override
    public TGMessage save(TargetJCRPath targetJCRPath) {
        throw new UnsupportedOperationException("This implementation does not support persistence");
    }
}
