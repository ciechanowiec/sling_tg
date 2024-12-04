package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

/**
 * Message in Telegram.
 */
public interface TGMessage extends StagedNode<TGMessage> {

    /**
     * {@link TGActor} who produced this {@link TGMessage}.
     * @return {@link TGActor} who produced this {@link TGMessage}
     */
    TGActor tgActor();

    /**
     * {@link TGAssets} of type {@link TGDocument} attached to this {@link TGMessage}.
     * @return {@link TGAssets} of type {@link TGDocument} attached to this {@link TGMessage}
     */
    TGAssets<TGDocument> tgDocuments();

    /**
     * {@link TGAssets} of type {@link TGPhoto} attached to this {@link TGMessage}.
     * @return {@link TGAssets} of type {@link TGPhoto} attached to this {@link TGMessage}
     */
    TGAssets<TGPhoto> tgPhotos();

    /**
     * {@link TGAssets} of type {@link TGVideo} attached to this {@link TGMessage}.
     * @return {@link TGAssets} of type {@link TGVideo} attached to this {@link TGMessage}
     */
    TGAssets<TGVideo> tgVideos();

    /**
     * {@link TGAssets} of type {@link TGAudio} attached to this {@link TGMessage}.
     * @return {@link TGAssets} of type {@link TGAudio} attached to this {@link TGMessage}
     */
    TGAssets<TGAudio> tgAudios();

    /**
     * {@link TGText} of this {@link TGMessage}.
     * @return {@link TGText} of this {@link TGMessage}
     */
    TGText tgText();

    /**
     * {@link TGMessageID} of this {@link TGMessage}.
     * @return {@link TGMessageID} of this {@link TGMessage}
     */
    TGMessageID tgMessageID();

    /**
     * Listable {@link TGCommand} passed with this {@link TGMessage}.
     * <p>
     * A listable {@link TGCommand} is a {@link TGCommand} that returns {@code true}
     * when {@link TGCommand#isListable()} is called.
     * @return listable {@link TGCommand} passed with this {@link TGMessage}
     */
    TGCommand tgCommand();

    /**
     * {@link TGSendingDate} that denotes when this {@link TGMessage} was sent.
     * @return {@link TGSendingDate} that denotes when this {@link TGMessage} was sent
     */
    TGSendingDate tgSendingDate();

    /**
     * {@link TGActivationStatus} of this {@link TGMessage}.
     * @return {@link TGActivationStatus} of this {@link TGMessage}
     */
    TGActivationStatus tgActivationStatus();
}
