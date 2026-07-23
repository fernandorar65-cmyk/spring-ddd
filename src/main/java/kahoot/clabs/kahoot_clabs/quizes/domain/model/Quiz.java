package kahoot.clabs.kahoot_clabs.quizes.domain.model;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quizes.domain.Enums.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.quizes.domain.Enums.QuizStatus;
import kahoot.clabs.kahoot_clabs.quizes.domain.Enums.QuizVisibility;
import kahoot.clabs.kahoot_clabs.quizes.domain.model.ValueObjects.EstimatedTime;

@Getter
public class Quiz {

    private final UUID id;
    private UUID organizationId;
    private UUID categoryId;
    private UUID createdById;          // User ID

    private String title;
    private String description;
    private String thumbnail;

    private QuizVisibility visibility;
    private QuizStatus status;
    private QuizDifficulty difficulty;
    private EstimatedTime estimatedTime;

    private List<Question> questions = new ArrayList<>();
    private QuizSettings settings;

    private int playCount = 0;
    private Double averageRating = 0.0;
    private boolean isTemplate = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Quiz(UUID id, UUID organizationId, String title, UUID createdById, QuizVisibility visibility, QuizStatus status, QuizDifficulty difficulty, EstimatedTime estimatedTime) {
        this.id = id != null ? id : UUID.randomUUID();
        this.organizationId = organizationId;
        this.title = title;
        this.createdById = createdById;
        this.visibility = visibility;
        this.status = status;
        this.difficulty = difficulty;
        this.estimatedTime = estimatedTime;
        this.status = QuizStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.settings = QuizSettings.defaultSettings();
    }

    public static Quiz create(UUID organizationId, String title, UUID createdById) {
        return new Quiz(null, organizationId, title, createdById, QuizVisibility.ORGANIZATION, QuizStatus.DRAFT, QuizDifficulty.EASY, null);
    }

    // Comportamiento del Aggregate
    public void addQuestion(Question question) {
        question.setOrderIndex(questions.size() + 1);
        questions.add(question);
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        this.status = QuizStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementPlayCount() {
        this.playCount++;
    }
}