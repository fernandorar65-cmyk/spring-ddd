package kahoot.clabs.kahoot_clabs.quizes.domain.model;
import lombok.Getter;
import java.util.UUID;

import io.swagger.v3.oas.models.media.MediaType;

@Getter
public class QuestionMedia {

    private final UUID id;
    private UUID questionId;

    private MediaType type;           // IMAGE, VIDEO, AUDIO, GIF, etc.
    private String url;               // URL del archivo (Azure Blob, etc.)
    private String thumbnailUrl;      // Miniatura (opcional)
    private String altText;           // Texto alternativo para accesibilidad

    private QuestionMedia(MediaType type, String url) {
        validate(type, url);
        this.id = UUID.randomUUID();
        this.type = type;
        this.url = url;
    }

    public static QuestionMedia create(MediaType type, String url) {
        return new QuestionMedia(type, url);
    }

    private void validate(MediaType type, String url) {
        if (type == null) {
            throw new IllegalArgumentException("El tipo de media es obligatorio");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL del archivo es obligatoria");
        }
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}