package kahoot.clabs.kahoot_clabs.gameplay.application.query;

import java.util.UUID;

public record GetGameSessionQuery(UUID organizationId, UUID sessionId) {
}
