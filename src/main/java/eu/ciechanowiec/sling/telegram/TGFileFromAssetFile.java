package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.AssetFile;
import eu.ciechanowiec.sling.rocket.unit.DataSize;
import eu.ciechanowiec.sling.telegram.api.TGFile;
import java.io.InputStream;

class TGFileFromAssetFile implements TGFile {

    private final AssetFile assetFile;

    TGFileFromAssetFile(AssetFile assetFile) {
        this.assetFile = assetFile;
    }

    @Override
    public InputStream retrieve() {
        return assetFile.retrieve();
    }

    @Override
    public DataSize size() {
        return assetFile.size();
    }
}
