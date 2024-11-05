package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.asset.Asset;

import java.util.Optional;

/**
 * Multimedia asset in Telegram.
 */
public interface TGAsset {

    /**
     * {@link TGFile} related with this {@link TGAsset}.
     * @return {@link TGFile} related with this {@link TGAsset}
     */
    TGFile tgFile();

    /**
     * {@link TGMetadata} related with this {@link TGAsset}.
     * @return {@link TGMetadata} related with this {@link TGAsset}
     */
    TGMetadata tgMetadata();

    /**
     * {@link Optional} containing the {@link Asset} related with this {@link TGAsset}.
     * If there is no such related {@link Asset}, then {@link Optional#empty()} is returned.
     * @return {@link Optional} containing the {@link Asset} related with this {@link TGAsset};
     *         if there is no such related {@link Asset}, then {@link Optional#empty()} is returned
     */
    Optional<Asset> asset();
}
