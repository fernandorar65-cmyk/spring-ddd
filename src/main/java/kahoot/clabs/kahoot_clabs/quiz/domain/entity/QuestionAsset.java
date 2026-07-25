package kahoot.clabs.kahoot_clabs.quiz.domain.entity;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.MediaUrl;
import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Media attached to a question (image, video, audio, ...).
 */
public class QuestionAsset extends BaseEntity {

    private UUID questionId;
    private final MediaType type;
    private final MediaUrl url;
    private MediaUrl thumbnailUrl;
    private String altText;

    private QuestionAsset(MediaType type, MediaUrl url) {
        super(null);
        if (type == null) {
            throw new DomainException("Media type is required");
        }
        if (url == null) {
            throw new DomainException("Media URL is required");
        }
        this.type = type;
        this.url = url;
    }

    public static QuestionAsset create(MediaType type, MediaUrl url) {
        return new QuestionAsset(type, url);
    }

    void assignQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    void changeThumbnail(MediaUrl thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    void changeAltText(String altText) {
        this.altText = altText;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public MediaType getType() {
        return type;
    }

    public MediaUrl getUrl() {
        return url;
    }

    public MediaUrl getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAltText() {
        return altText;
    }
}
