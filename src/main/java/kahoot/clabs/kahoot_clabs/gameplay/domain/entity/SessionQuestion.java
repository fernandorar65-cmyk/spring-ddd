package kahoot.clabs.kahoot_clabs.gameplay.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Snapshot of a quiz question inside a session. quizQuestionId references the quiz
 * bounded context; points and time limit are copied so a later quiz edit cannot
 * change a session already played.
 */
public class SessionQuestion extends BaseEntity {

    private final UUID gameSessionId;
    private final UUID quizQuestionId;
    private final int orderIndex;
    private final int points;
    private final int timeLimitSeconds;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private final List<PlayerAnswer> answers = new ArrayList<>();

    private SessionQuestion(UUID id, UUID gameSessionId, UUID quizQuestionId, int orderIndex, int points,
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
        this.orderIndex = orderIndex;
        this.points = points;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public static SessionQuestion of(UUID gameSessionId, UUID quizQuestionId, int orderIndex, int points,
                                     int timeLimitSeconds) {
        return new SessionQuestion(null, gameSessionId, quizQuestionId, orderIndex, points, timeLimitSeconds);
    }

    public static SessionQuestion rehydrate(UUID id, UUID gameSessionId, UUID quizQuestionId, int orderIndex,
                                            int points, int timeLimitSeconds) {
        return new SessionQuestion(id, gameSessionId, quizQuestionId, orderIndex, points, timeLimitSeconds);
    }

    public static SessionQuestion rehydrate(
            UUID id,
            UUID gameSessionId,
            UUID quizQuestionId,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            List<PlayerAnswer> answers) {
        SessionQuestion question = new SessionQuestion(
                id, gameSessionId, quizQuestionId, orderIndex, points, timeLimitSeconds);
        question.openedAt = openedAt;
        question.closedAt = closedAt;
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

    public boolean isOpen() {
        return openedAt != null && closedAt == null;
    }

    public UUID getGameSessionId() {
        return gameSessionId;
    }

    public UUID getQuizQuestionId() {
        return quizQuestionId;
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
}
