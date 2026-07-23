package kahoot.clabs.kahoot_clabs.users.domain.model.Event;
import kahoot.clabs.kahoot_clabs.users.domain.model.User;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
public class UserCreatedEvent {

    private final UUID userId;
    private final UUID organizationId;
    private final String email;
    private final LocalDateTime occurredOn;

    public UserCreatedEvent(User user) {
        this.userId = user.getId();
        this.organizationId = user.getOrganizationId();
        this.email = user.getEmail().getValue();
        this.occurredOn = LocalDateTime.now();
    }
}