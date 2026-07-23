package kahoot.clabs.kahoot_clabs.users.domain.event;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainEvent;

public class UserCreatedEvent extends DomainEvent {

    private final UUID userId;
    private final UUID organizationId;
    private final String email;

    public UserCreatedEvent(UUID userId, UUID organizationId, String email) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getEmail() {
        return email;
    }
}
