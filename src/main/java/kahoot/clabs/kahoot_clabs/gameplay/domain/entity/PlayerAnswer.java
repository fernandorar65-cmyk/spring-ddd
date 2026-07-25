package kahoot.clabs.kahoot_clabs.gameplay.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.ResponseTime;
import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Answer submitted by a player. selectedOptionId references an AnswerOption of the
 * quiz bounded context; correctness is resolved before reaching this entity.
 */
public class PlayerAnswer extends BaseEntity {

    private final UUID sessionQuestionId;
    private final UUID sessionPlayerId;
    private final UUID selectedOptionId;
    private final boolean correct;
    private final ResponseTime responseTime;
    private final int awardedPoints;
    private final LocalDateTime answeredAt;

    private PlayerAnswer(UUID id, UUID sessionQuestionId, UUID sessionPlayerId, UUID selectedOptionId,
                         boolean correct, ResponseTime responseTime, int awardedPoints, LocalDateTime answeredAt) {
        super(id);
        if (sessionQuestionId == null) {
            throw new DomainException("Session question id is required");
        }
        if (sessionPlayerId == null) {
            throw new DomainException("Session player id is required");
        }
        if (responseTime == null) {
            throw new DomainException("Response time is required");
        }
        if (awardedPoints < 0) {
            throw new DomainException("Awarded points cannot be negative");
        }
        this.sessionQuestionId = sessionQuestionId;
        this.sessionPlayerId = sessionPlayerId;
        this.selectedOptionId = selectedOptionId;
        this.correct = correct;
        this.responseTime = responseTime;
        this.awardedPoints = awardedPoints;
        this.answeredAt = answeredAt != null ? answeredAt : LocalDateTime.now();
    }

    public static PlayerAnswer of(UUID sessionQuestionId, UUID sessionPlayerId, UUID selectedOptionId,
                                  boolean correct, ResponseTime responseTime, int awardedPoints) {
        return new PlayerAnswer(null, sessionQuestionId, sessionPlayerId, selectedOptionId, correct, responseTime,
                awardedPoints, null);
    }

    public static PlayerAnswer rehydrate(UUID id, UUID sessionQuestionId, UUID sessionPlayerId, UUID selectedOptionId,
                                         boolean correct, ResponseTime responseTime, int awardedPoints) {
        return new PlayerAnswer(id, sessionQuestionId, sessionPlayerId, selectedOptionId, correct, responseTime,
                awardedPoints, null);
    }

    public static PlayerAnswer rehydrate(
            UUID id,
            UUID sessionQuestionId,
            UUID sessionPlayerId,
            UUID selectedOptionId,
            boolean correct,
            ResponseTime responseTime,
            int awardedPoints,
            LocalDateTime answeredAt) {
        return new PlayerAnswer(
                id,
                sessionQuestionId,
                sessionPlayerId,
                selectedOptionId,
                correct,
                responseTime,
                awardedPoints,
                answeredAt);
    }

    public UUID getSessionQuestionId() {
        return sessionQuestionId;
    }

    public UUID getSessionPlayerId() {
        return sessionPlayerId;
    }

    public UUID getSelectedOptionId() {
        return selectedOptionId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public ResponseTime getResponseTime() {
        return responseTime;
    }

    public int getAwardedPoints() {
        return awardedPoints;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
}
