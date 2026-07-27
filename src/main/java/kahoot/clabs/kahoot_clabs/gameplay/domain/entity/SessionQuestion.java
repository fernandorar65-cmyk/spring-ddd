package kahoot.clabs.kahoot_clabs.gameplay.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.ResponseTime;
import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Immutable snapshot of a quiz question inside a session. The original question
 * id is retained only for traceability; title, type, timing, points and options
 * are copied so later Quiz edits cannot rewrite historical gameplay.
 */
public class SessionQuestion extends BaseEntity {

    private final UUID gameSessionId;
    private final UUID quizQuestionId;
    private final String title;
    private final String description;
    private final String questionType;
    private final int orderIndex;
    private final int points;
    private final int timeLimitSeconds;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private final List<SessionAnswerOption> options = new ArrayList<>();
    private final List<PlayerAnswer> answers = new ArrayList<>();

    private SessionQuestion(
            UUID id,
            UUID gameSessionId,
            UUID quizQuestionId,
            String title,
            String description,
            String questionType,
            int orderIndex,
            int points,
            int timeLimitSeconds) {
        super(id);
        if (gameSessionId == null) {
            throw new DomainException("Game session id is required");
        }
        if (quizQuestionId == null) {
            throw new DomainException("Quiz question id is required");
        }
        if (orderIndex < 1) {
            throw new DomainException("Question order index must be at least 1");
        }
        if (points < 0) {
            throw new DomainException("Question points cannot be negative");
        }
        if (timeLimitSeconds <= 0) {
            throw new DomainException("Question time limit must be positive");
        }
        this.gameSessionId = gameSessionId;
        this.quizQuestionId = quizQuestionId;
        this.title = title;
        this.description = description;
        this.questionType = questionType;
        this.orderIndex = orderIndex;
        this.points = points;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public static SessionQuestion snapshot(
            UUID gameSessionId,
            UUID quizQuestionId,
            String title,
            String description,
            String questionType,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            List<AnswerOptionSnapshot> optionSnapshots) {
        if (title == null || title.isBlank() || questionType == null || questionType.isBlank()) {
            throw new DomainException("Question snapshot title and type are required");
        }
        SessionQuestion question = new SessionQuestion(
                null,
                gameSessionId,
                quizQuestionId,
                title,
                description,
                questionType,
                orderIndex,
                points,
                timeLimitSeconds);
        if (optionSnapshots != null) {
            optionSnapshots.forEach(snapshot -> question.options.add(SessionAnswerOption.snapshot(question.getId(), snapshot)));
        }
        return question;
    }

    public static SessionQuestion rehydrateSnapshot(
            UUID id,
            UUID gameSessionId,
            UUID quizQuestionId,
            String title,
            String description,
            String questionType,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            List<SessionAnswerOption> options,
            List<PlayerAnswer> answers) {
        SessionQuestion question = new SessionQuestion(
                id,
                gameSessionId,
                quizQuestionId,
                title,
                description,
                questionType,
                orderIndex,
                points,
                timeLimitSeconds);
        question.openedAt = openedAt;
        question.closedAt = closedAt;
        if (options != null) {
            question.options.addAll(options);
        }
        if (answers != null) {
            question.answers.addAll(answers);
        }
        return question;
    }

    public void open() {
        if (openedAt != null) {
            throw new DomainException("Question was already opened");
        }
        this.openedAt = LocalDateTime.now();
    }

    public void close() {
        if (openedAt == null) {
            throw new DomainException("Cannot close a question that was never opened");
        }
        if (closedAt != null) {
            throw new DomainException("Question was already closed");
        }
        this.closedAt = LocalDateTime.now();
    }

    public void register(PlayerAnswer answer) {
        if (openedAt == null || closedAt != null) {
            throw new DomainException("Answers are only accepted while the question is open");
        }
        if (hasAnswerFrom(answer.getSessionPlayerId())) {
            throw new DomainException("Player already answered this question");
        }
        answers.add(answer);
    }

    public boolean hasAnswerFrom(UUID sessionPlayerId) {
        return answers.stream().anyMatch(answer -> answer.getSessionPlayerId().equals(sessionPlayerId));
    }

    public boolean isCorrectOption(UUID sessionAnswerOptionId) {
        if (sessionAnswerOptionId == null) {
            throw new DomainException("Session answer option id is required");
        }
        return options.stream()
                .filter(option -> option.getId().equals(sessionAnswerOptionId))
                .findFirst()
                .map(SessionAnswerOption::isCorrect)
                .orElseThrow(() -> new DomainException("Answer option does not belong to the current question"));
    }

    public ResponseTime responseTimeAt(LocalDateTime answeredAt) {
        if (openedAt == null || answeredAt == null) {
            throw new DomainException("Question must be open to calculate response time");
        }
        return ResponseTime.ofMillis(java.time.Duration.between(openedAt, answeredAt).toMillis());
    }

    public boolean isOpen() {
        return openedAt != null && closedAt == null;
    }

    public UUID getGameSessionId() {
        return gameSessionId;
    }

    public UUID getQuizQuestionId() {
        return quizQuestionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getQuestionType() {
        return questionType;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public int getPoints() {
        return points;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public List<PlayerAnswer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public List<SessionAnswerOption> getOptions() {
        return Collections.unmodifiableList(options);
    }
}
