package kahoot.clabs.kahoot_clabs.identity.application.event;

import java.util.UUID;

/** Application event: user removed from write side; delete from read model. */
public record UserReadModelDeletedEvent(UUID userId) {
}
