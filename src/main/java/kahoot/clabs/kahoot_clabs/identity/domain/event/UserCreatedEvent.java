package kahoot.clabs.kahoot_clabs.identity.domain.event;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainEvent;

/**
 * Parked domain event for a future user-created pipeline.
 * Not registered, published or consumed in the MVP.
 *
 * @see docs/domain-events.md
 */
public class UserCreatedEvent extends DomainEvent {

    private final UUID userId;
    private final String email;

    public UserCreatedEvent(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
