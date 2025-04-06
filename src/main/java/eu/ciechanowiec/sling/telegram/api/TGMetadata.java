package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.asset.AssetMetadata;
import javax.jcr.Property;

/**
 * {@link AssetMetadata} in Telegram.
 */
public interface TGMetadata extends AssetMetadata {

    /**
     * Name of a {@link Property} where an original name of a {@link TGFile} related with this {@link TGMetadata} is
     * persisted.
     */
    String PN_ORIGINAL_FILE_NAME = "originalFileName";

    /**
     * Original name of the {@link TGFile} related with this {@link TGMetadata}.
     *
     * @return original name of the {@link TGFile} related with this {@link TGMetadata}
     */
    String originalFileName();
}
