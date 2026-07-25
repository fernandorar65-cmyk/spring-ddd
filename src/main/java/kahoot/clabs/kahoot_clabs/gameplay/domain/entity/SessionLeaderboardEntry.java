package kahoot.clabs.kahoot_clabs.gameplay.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Persisted leaderboard projection. It is reconstructable from session players
 * and answers, so it is never the source of truth for scoring.
 */
public final class SessionLeaderboardEntry extends BaseEntity {

    private final UUID sessionId;
    private final UUID sessionPlayerId;
    private final int position;
    private final String nickname;
    private final int score;
    private final int correctAnswers;
    private final LocalDateTime updatedAt;

    private SessionLeaderboardEntry(
            UUID id,
            UUID sessionId,
            UUID sessionPlayerId,
            int position,
            String nickname,
            int score,
            int correctAnswers,
            LocalDateTime updatedAt) {
        super(id);
        if (sessionId == null || sessionPlayerId == null) {
            throw new DomainException("Session and player ids are required");
        }
        if (position < 1) {
            throw new DomainException("Leaderboard position must be at least 1");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new DomainException("Leaderboard nickname is required");
        }
        if (score < 0 || correctAnswers < 0) {
            throw new DomainException("Leaderboard values cannot be negative");
        }
        this.sessionId = sessionId;
        this.sessionPlayerId = sessionPlayerId;
        this.position = position;
        this.nickname = nickname.trim();
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static SessionLeaderboardEntry create(
            UUID sessionId,
            UUID sessionPlayerId,
            int position,
            String nickname,
            int score,
            int correctAnswers) {
        return new SessionLeaderboardEntry(
                null, sessionId, sessionPlayerId, position, nickname, score, correctAnswers, null);
    }

    public static SessionLeaderboardEntry rehydrate(
            UUID id,
            UUID sessionId,
            UUID sessionPlayerId,
            int position,
            String nickname,
            int score,
            int correctAnswers,
            LocalDateTime updatedAt) {
        return new SessionLeaderboardEntry(
                id, sessionId, sessionPlayerId, position, nickname, score, correctAnswers, updatedAt);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getSessionPlayerId() {
        return sessionPlayerId;
    }

    public int getPosition() {
        return position;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
