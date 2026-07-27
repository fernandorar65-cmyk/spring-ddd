package kahoot.clabs.kahoot_clabs.gameplay.domain.event;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainEvent;

/**
 * Reserved domain event: raised conceptually when a game session starts.
 * Not published or consumed yet (no event bus / outbox / listeners).
 */
public class GameStartedEvent extends DomainEvent {

    private final UUID gameSessionId;
    private final UUID organizationId;
    private final UUID quizId;
    private final UUID hostUserId;
    private final String pin;

    public GameStartedEvent(UUID gameSessionId, UUID organizationId, UUID quizId, UUID hostUserId, String pin) {
        this.gameSessionId = gameSessionId;
        this.organizationId = organizationId;
        this.quizId = quizId;
        this.hostUserId = hostUserId;
        this.pin = pin;
    }

    public UUID getGameSessionId() {
        return gameSessionId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public String getPin() {
        return pin;
    }
}
