package kahoot.clabs.kahoot_clabs.gameplay.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.PlayerScore;
import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Player taking part in a session. userId is null for guests, and points to the
 * identity context when the player is a registered user.
 */
public class SessionPlayer extends BaseEntity {

    private static final int MAX_NICKNAME_LENGTH = 30;

    private final UUID gameSessionId;
    private final UUID userId;
    private final String nickname;
    private PlayerScore score;
    private boolean connected;
    private final LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    private SessionPlayer(UUID id, UUID gameSessionId, UUID userId, String nickname, PlayerScore score,
                          boolean connected, LocalDateTime joinedAt) {
        super(id);
        if (gameSessionId == null) {
            throw new DomainException("Game session id is required");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new DomainException("Nickname is required");
        }
        if (nickname.trim().length() > MAX_NICKNAME_LENGTH) {
            throw new DomainException("Nickname cannot exceed " + MAX_NICKNAME_LENGTH + " characters");
        }
        this.gameSessionId = gameSessionId;
        this.userId = userId;
        this.nickname = nickname.trim();
        this.score = score != null ? score : PlayerScore.zero();
        this.connected = connected;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
    }

    public static SessionPlayer join(UUID gameSessionId, UUID userId, String nickname) {
        return new SessionPlayer(null, gameSessionId, userId, nickname, PlayerScore.zero(), true, null);
    }

    public static SessionPlayer rehydrate(UUID id, UUID gameSessionId, UUID userId, String nickname,
                                          PlayerScore score, boolean connected, LocalDateTime joinedAt) {
        return new SessionPlayer(id, gameSessionId, userId, nickname, score, connected, joinedAt);
    }

    public static SessionPlayer rehydrate(
            UUID id,
            UUID gameSessionId,
            UUID userId,
            String nickname,
            PlayerScore score,
            boolean connected,
            LocalDateTime joinedAt,
            LocalDateTime leftAt) {
        SessionPlayer player = new SessionPlayer(
                id, gameSessionId, userId, nickname, score, connected, joinedAt);
        player.leftAt = leftAt;
        return player;
    }

    public void award(int points) {
        this.score = score.plus(points);
    }

    public void disconnect() {
        this.connected = false;
        this.leftAt = LocalDateTime.now();
    }

    public void reconnect() {
        this.connected = true;
        this.leftAt = null;
    }

    public UUID getGameSessionId() {
        return gameSessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public PlayerScore getScore() {
        return score;
    }

    public boolean isConnected() {
        return connected;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }
}
