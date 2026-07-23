package kahoot.clabs.kahoot_clabs.quizzes.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.QuestionType;
import kahoot.clabs.kahoot_clabs.quizzes.domain.enums.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.Points;
import kahoot.clabs.kahoot_clabs.quizzes.domain.model.valueobject.TimeLimit;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class Question {

    private final UUID id;
    private UUID quizId;
    private String title;
    private String description;
    private final QuestionType type;
    private Points points;
    private TimeLimit timeLimit;
    private int orderIndex;
    private String explanation;
    private final List<AnswerOption> options = new ArrayList<>();
    private QuestionMedia media;
    private QuizDifficulty difficulty;

    Question(String title, QuestionType type) {
        if (title == null || title.isBlank()) {
            throw new DomainException("Question title is required");
        }
        if (type == null) {
            throw new DomainException("Question type is required");
        }
        this.id = UUID.randomUUID();
        this.title = title.trim();
        this.type = type;
        this.points = Points.defaultValue();
        this.timeLimit = TimeLimit.defaultValue();
        this.difficulty = QuizDifficulty.EASY;
    }

    void assignQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    void assignOrderIndex(int orderIndex) {
        if (orderIndex < 1) {
            throw new DomainException("Question order index must be at least 1");
        }
        this.orderIndex = orderIndex;
    }

    void addAnswerOption(AnswerOption option) {
        option.assignQuestionId(this.id);
        option.assignOrderIndex(options.size() + 1);
        options.add(option);
    }

    void attachMedia(QuestionMedia media) {
        media.assignQuestionId(this.id);
        this.media = media;
    }

    void changeDifficulty(QuizDifficulty difficulty) {
        if (difficulty == null) {
            throw new DomainException("Question difficulty is required");
        }
        this.difficulty = difficulty;
    }

    void changePoints(Points points) {
        if (points == null) {
            throw new DomainException("Points are required");
        }
        this.points = points;
    }

    void changeTimeLimit(TimeLimit timeLimit) {
        if (timeLimit == null) {
            throw new DomainException("Time limit is required");
        }
        this.timeLimit = timeLimit;
    }

    void assertReadyToPublish() {
        if (title == null || title.isBlank()) {
            throw new DomainException("Every question must have a title before publishing");
        }

        switch (type) {
            case SHORT_ANSWER -> {
                // Free text; options optional
            }
            case TRUE_FALSE -> {
                if (options.size() != 2) {
                    throw new DomainException("True/False questions must have exactly 2 options");
                }
                assertHasAtLeastOneCorrectOption();
            }
            case MULTIPLE_CHOICE -> {
                if (options.size() < 2) {
                    throw new DomainException("Multiple choice questions need at least 2 options");
                }
                long correctCount = options.stream().filter(AnswerOption::isCorrect).count();
                if (correctCount != 1) {
                    throw new DomainException("Multiple choice questions must have exactly one correct option");
                }
            }
            case MULTIPLE_SELECT -> {
                if (options.size() < 2) {
                    throw new DomainException("Multiple select questions need at least 2 options");
                }
                assertHasAtLeastOneCorrectOption();
            }
        }
    }

    private void assertHasAtLeastOneCorrectOption() {
        boolean hasCorrect = options.stream().anyMatch(AnswerOption::isCorrect);
        if (!hasCorrect) {
            throw new DomainException("Question '" + title + "' must have at least one correct option");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public QuestionType getType() {
        return type;
    }

    public Points getPoints() {
        return points;
    }

    public TimeLimit getTimeLimit() {
        return timeLimit;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<AnswerOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public QuestionMedia getMedia() {
        return media;
    }

    public QuizDifficulty getDifficulty() {
        return difficulty;
    }
}
