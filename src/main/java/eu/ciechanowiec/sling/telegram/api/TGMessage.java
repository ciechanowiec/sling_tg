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
     * {@link TGDocuments} attached to this {@link TGMessage}.
     * @return {@link TGDocuments} attached to this {@link TGMessage}
     */
    TGDocuments tgDocuments();

    /**
     * {@link TGPhotos} attached to this {@link TGMessage}.
     * @return {@link TGPhotos} attached to this {@link TGMessage}
     */
    TGPhotos tgPhotos();

    /**
     * {@link TGVideos} attached to this {@link TGMessage}.
     * @return {@link TGVideos} attached to this {@link TGMessage}
     */
    TGVideos tgVideos();

    /**
     * {@link TGAudios} attached to this {@link TGMessage}.
     * @return {@link TGAudios} attached to this {@link TGMessage}
     */
    TGAudios tgAudios();

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
     * {@link TGCommand} passed with this {@link TGMessage}.
     * @return {@link TGCommand} passed with this {@link TGMessage}
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
