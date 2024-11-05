package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.asset.Asset;
import eu.ciechanowiec.sling.rocket.asset.FileMetadata;
import eu.ciechanowiec.sling.rocket.jcr.DefaultProperties;
import eu.ciechanowiec.sling.rocket.jcr.NodeProperties;
import eu.ciechanowiec.sling.telegram.api.TGMetadata;
import jakarta.ws.rs.core.MediaType;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@ToString
class TGMetadataBasic implements TGMetadata {

    @ToString.Exclude
    private final Supplier<String> originalFileNameSupplier;
    @ToString.Exclude
    private final Supplier<String> mimeTypeSupplier;
    @ToString.Exclude
    private final Supplier<Map<String, String>> allSupplier;
    @ToString.Exclude
    private final Supplier<Optional<NodeProperties>> propertiesSupplier;

    TGMetadataBasic(Document document, Supplier<String> fallbackMimeTypeSupplier) {
        this.originalFileNameSupplier = () -> Optional.ofNullable(document.getFileName()).orElse(StringUtils.EMPTY);
        this.mimeTypeSupplier = Optional.ofNullable(document.getMimeType())
                .map(mimeType -> (Supplier<String>) () -> mimeType)
                .orElse(fallbackMimeTypeSupplier);
        this.allSupplier = () -> Map.of(
                PN_MIME_TYPE, mimeType(),
                PN_ORIGINAL_FILE_NAME, originalFileName()
        );
        this.propertiesSupplier = Optional::empty;
        log.trace("Initialized {}", this);
    }

    TGMetadataBasic(Video video, Supplier<String> fallbackMimeTypeSupplier) {
        this.originalFileNameSupplier = () -> Optional.ofNullable(video.getFileName()).orElse(StringUtils.EMPTY);
        this.mimeTypeSupplier = Optional.ofNullable(video.getMimeType())
                .map(mimeType -> (Supplier<String>) () -> mimeType)
                .orElse(fallbackMimeTypeSupplier);
        this.allSupplier = () -> Map.of(
                PN_MIME_TYPE, mimeType(),
                PN_ORIGINAL_FILE_NAME, originalFileName()
        );
        this.propertiesSupplier = Optional::empty;
        log.trace("Initialized {}", this);
    }

    TGMetadataBasic(Audio audio, Supplier<String> fallbackMimeTypeSupplier) {
        this.originalFileNameSupplier = () -> Optional.ofNullable(audio.getFileName()).orElse(StringUtils.EMPTY);
        this.mimeTypeSupplier = Optional.ofNullable(audio.getMimeType())
                .map(mimeType -> (Supplier<String>) () -> mimeType)
                .orElse(fallbackMimeTypeSupplier);
        this.allSupplier = () -> Map.of(
                PN_MIME_TYPE, mimeType(),
                PN_ORIGINAL_FILE_NAME, originalFileName()
        );
        this.propertiesSupplier = Optional::empty;
        log.trace("Initialized {}", this);
    }

    TGMetadataBasic(Asset asset) {
        originalFileNameSupplier = () -> properties().flatMap(
                nodeProperties -> nodeProperties.propertyValue(PN_ORIGINAL_FILE_NAME, DefaultProperties.STRING_CLASS)
        ).orElse(StringUtils.EMPTY);
        mimeTypeSupplier = () -> properties().flatMap(
                nodeProperties -> nodeProperties.propertyValue(PN_MIME_TYPE, DefaultProperties.STRING_CLASS)
        ).orElse(MediaType.WILDCARD);
        allSupplier = () -> properties().map(NodeProperties::all).orElse(Map.of());
        propertiesSupplier = () -> asset.assetMetadata().properties();
        log.trace("Initialized {}", this);
    }

    @SuppressWarnings("squid:S1109")
    TGMetadataBasic(FileMetadata fileMetadata) {
        originalFileNameSupplier = () -> StringUtils.EMPTY;
        mimeTypeSupplier = fileMetadata::mimeType;
        allSupplier = () -> Collections.unmodifiableMap(new HashMap<>(fileMetadata.all()) {{
            put(PN_ORIGINAL_FILE_NAME, originalFileName());
        }});
        propertiesSupplier = Optional::empty;
    }

    @Override
    public String originalFileName() {
        return originalFileNameSupplier.get();
    }

    @Override
    public String mimeType() {
        return mimeTypeSupplier.get();
    }

    @Override
    public Map<String, String> all() {
        return allSupplier.get();
    }

    @Override
    public Optional<NodeProperties> properties() {
        return propertiesSupplier.get();
    }
}
