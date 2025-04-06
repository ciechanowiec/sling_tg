package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.unit.DataSize;
import eu.ciechanowiec.sling.rocket.unit.DataUnit;
import java.util.Optional;
import java.util.function.Supplier;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.Voice;

@SuppressWarnings("PMD.DataClass")
class WithOriginalMetadata {

    private final Supplier<String> fileID;
    private final Supplier<Optional<String>> originalMimeType;
    private final Supplier<Optional<String>> originalFileName;
    private final Supplier<Optional<DataSize>> originalDataSize;

    WithOriginalMetadata(Document document) {
        this.fileID = document::getFileId;
        this.originalMimeType = () -> Optional.ofNullable(document.getMimeType());
        this.originalFileName = () -> Optional.ofNullable(document.getFileName());
        this.originalDataSize = () -> Optional.ofNullable(document.getFileSize())
            .map(size -> new DataSize(size, DataUnit.BYTES));
    }

    WithOriginalMetadata(Audio audio) {
        this.fileID = audio::getFileId;
        this.originalMimeType = () -> Optional.ofNullable(audio.getMimeType());
        this.originalFileName = () -> Optional.ofNullable(audio.getFileName());
        this.originalDataSize = () -> Optional.ofNullable(audio.getFileSize())
            .map(size -> new DataSize(size, DataUnit.BYTES));
    }

    WithOriginalMetadata(Video video) {
        this.fileID = video::getFileId;
        this.originalMimeType = () -> Optional.ofNullable(video.getMimeType());
        this.originalFileName = () -> Optional.ofNullable(video.getFileName());
        this.originalDataSize = () -> Optional.ofNullable(video.getFileSize())
            .map(size -> new DataSize(size, DataUnit.BYTES));
    }

    WithOriginalMetadata(Voice voice) {
        this.fileID = voice::getFileId;
        this.originalMimeType = () -> Optional.ofNullable(voice.getMimeType());
        this.originalFileName = Optional::empty;
        this.originalDataSize = () -> Optional.ofNullable(voice.getFileSize())
            .map(size -> new DataSize(size, DataUnit.BYTES));
    }

    String fileID() {
        return fileID.get();
    }

    Optional<String> originalMimeType() {
        return originalMimeType.get();
    }

    Optional<String> originalFileName() {
        return originalFileName.get();
    }

    Optional<DataSize> originalDataSize() {
        return originalDataSize.get();
    }
}
