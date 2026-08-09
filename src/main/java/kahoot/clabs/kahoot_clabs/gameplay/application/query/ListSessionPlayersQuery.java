package kahoot.clabs.kahoot_clabs.gameplay.application.query;

import java.util.UUID;

public record ListSessionPlayersQuery(UUID organizationId, UUID sessionId) {
}
