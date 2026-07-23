package kahoot.clabs.kahoot_clabs.quizzes.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.MediaType;
import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.QuestionType;
import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.QuizStatus;
import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.QuizVisibility;
import kahoot.clabs.kahoot_clabs.quizzes.domain.event.QuizPublishedEvent;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.EstimatedTime;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.MediaUrl;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.Points;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.QuizTitle;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.TimeLimit;
import kahoot.clabs.kahoot_clabs.shared.domain.AggregateRoot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class Quiz extends AggregateRoot {

    private final UUID id;
    private final UUID organizationId;
    private UUID categoryId;
    private final UUID createdById;

    private QuizTitle title;
    private String description;
    private String thumbnail;

    private QuizVisibility visibility;
    private QuizStatus status;
    private QuizDifficulty difficulty;
    private EstimatedTime estimatedTime;

    private final List<Question> questions = new ArrayList<>();
    private QuizSettings settings;

    private int playCount;
    private double averageRating;
    private boolean template;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Quiz(UUID organizationId, QuizTitle title, UUID createdById) {
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (createdById == null) {
            throw new DomainException("CreatedBy id is required");
        }
        this.id = UUID.randomUUID();
        this.organizationId = organizationId;
        this.title = title;
        this.createdById = createdById;
        this.visibility = QuizVisibility.ORGANIZATION;
        this.status = QuizStatus.DRAFT;
        this.difficulty = QuizDifficulty.EASY;
        this.settings = QuizSettings.defaultSettings();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Quiz create(UUID organizationId, String title, UUID createdById) {
        return new Quiz(organizationId, QuizTitle.of(title), createdById);
    }

    public Question addQuestion(String questionTitle, QuestionType type) {
        ensureEditable();
        Question question = new Question(questionTitle, type);
        question.assignQuizId(this.id);
        question.assignOrderIndex(questions.size() + 1);
        questions.add(question);
        touch();
        return question;
    }

    public void addAnswerOption(UUID questionId, String text, boolean correct) {
        ensureEditable();
        Question question = requireQuestion(questionId);
        question.addAnswerOption(AnswerOption.create(text, correct));
        touch();
    }

    public void attachMedia(UUID questionId, MediaType type, String url) {
        ensureEditable();
        Question question = requireQuestion(questionId);
        question.attachMedia(QuestionMedia.create(type, MediaUrl.of(url)));
        touch();
    }

    public void changeQuestionDifficulty(UUID questionId, QuizDifficulty difficulty) {
        ensureEditable();
        requireQuestion(questionId).changeDifficulty(difficulty);
        touch();
    }

    public void changeQuestionPoints(UUID questionId, int points) {
        ensureEditable();
        requireQuestion(questionId).changePoints(Points.of(points));
        touch();
    }

    public void changeQuestionTimeLimit(UUID questionId, int seconds) {
        ensureEditable();
        requireQuestion(questionId).changeTimeLimit(TimeLimit.ofSeconds(seconds));
        touch();
    }

    public void rename(String newTitle) {
        ensureEditable();
        this.title = QuizTitle.of(newTitle);
        touch();
    }

    public void changeDescription(String description) {
        ensureEditable();
        this.description = description;
        touch();
    }

    public void changeVisibility(QuizVisibility visibility) {
        if (visibility == null) {
            throw new DomainException("Visibility is required");
        }
        this.visibility = visibility;
        touch();
    }

    public void changeDifficulty(QuizDifficulty difficulty) {
        ensureEditable();
        if (difficulty == null) {
            throw new DomainException("Difficulty is required");
        }
        this.difficulty = difficulty;
        touch();
    }

    public void changeEstimatedTime(EstimatedTime estimatedTime) {
        ensureEditable();
        this.estimatedTime = estimatedTime;
        touch();
    }

    public void changeSettings(QuizSettings settings) {
        ensureEditable();
        if (settings == null) {
            throw new DomainException("Settings are required");
        }
        this.settings = settings;
        touch();
    }

    public void publish() {
        if (status == QuizStatus.ARCHIVED) {
            throw new DomainException("Archived quizzes cannot be published");
        }
        if (questions.isEmpty()) {
            throw new DomainException("Cannot publish a quiz without questions");
        }
        for (Question question : questions) {
            question.assertReadyToPublish();
        }
        this.status = QuizStatus.PUBLISHED;
        touch();
        registerEvent(new QuizPublishedEvent(id, organizationId, createdById));
    }

    public void archive() {
        this.status = QuizStatus.ARCHIVED;
        touch();
    }

    public void incrementPlayCount() {
        if (status != QuizStatus.PUBLISHED) {
            throw new DomainException("Only published quizzes can be played");
        }
        this.playCount++;
        touch();
    }

    private Question requireQuestion(UUID questionId) {
        return questions.stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Question not found: " + questionId));
    }

    private void ensureEditable() {
        if (status == QuizStatus.ARCHIVED) {
            throw new DomainException("Archived quizzes cannot be modified");
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public QuizTitle getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public QuizVisibility getVisibility() {
        return visibility;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public QuizDifficulty getDifficulty() {
        return difficulty;
    }

    public EstimatedTime getEstimatedTime() {
        return estimatedTime;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public QuizSettings getSettings() {
        return settings;
    }

    public int getPlayCount() {
        return playCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public boolean isTemplate() {
        return template;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
