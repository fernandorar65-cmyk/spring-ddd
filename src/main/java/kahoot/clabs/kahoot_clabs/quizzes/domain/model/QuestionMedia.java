package kahoot.clabs.kahoot_clabs.quizzes.domain.model;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.MediaType;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.MediaUrl;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class QuestionMedia {

    private final UUID id;
    private UUID questionId;
    private final MediaType type;
    private final MediaUrl url;
    private MediaUrl thumbnailUrl;
    private String altText;

    private QuestionMedia(MediaType type, MediaUrl url) {
        if (type == null) {
            throw new DomainException("Media type is required");
        }
        if (url == null) {
            throw new DomainException("Media URL is required");
        }
        this.id = UUID.randomUUID();
        this.type = type;
        this.url = url;
    }

    static QuestionMedia create(MediaType type, MediaUrl url) {
        return new QuestionMedia(type, url);
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

    public UUID getId() {
        return id;
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
