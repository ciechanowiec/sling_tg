package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.StagedNode;

import java.util.Optional;

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
     * {@link TGAssets} of type {@link TGPhoto} attached to this {@link TGMessage}.
     * @return {@link TGAssets} of type {@link TGPhoto} attached to this {@link TGMessage}
     */
    TGAssets<TGPhoto> tgPhotos();

    /**
     * {@link Optional} with a {@link TGDocument} attached to this {@link TGMessage}.
     * If there is no such {@link TGDocument}, an empty {@link Optional} is returned.
     * @return {@link Optional} with a {@link TGDocument} attached to this {@link TGMessage};
     *         if there is no such {@link TGDocument}, an empty {@link Optional} is returned
     */
    Optional<TGDocument> tgDocument();

    /**
     * {@link Optional} with a {@link TGVideo} attached to this {@link TGMessage}. If there is no such {@link TGVideo},
     * an empty {@link Optional} is returned.
     * @return {@link Optional} with a {@link TGVideo} attached to this {@link TGMessage};
     *         if there is no such {@link TGVideo}, an empty {@link Optional} is returned
     */
    Optional<TGVideo> tgVideo();

    /**
     * {@link Optional} with a {@link TGVideo} attached to this {@link TGMessage}. If there is no such {@link TGAudio},
     * an empty {@link Optional} is returned.
     * @return {@link Optional} with a {@link TGAudio} attached to this {@link TGMessage};
     *         if there is no such {@link TGAudio}, an empty {@link Optional} is returned
     */
    Optional<TGAudio> tgAudio();

    /**
     * {@link Optional} with a {@link TGVoice} attached to this {@link TGMessage}. If there is no such {@link TGVoice},
     * an empty {@link Optional} is returned.
     * @return {@link Optional} with a {@link TGVoice} attached to this {@link TGMessage};
     *         if there is no such {@link TGVoice}, an empty {@link Optional} is returned
     */
    Optional<TGVoice> tgVoice();

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
