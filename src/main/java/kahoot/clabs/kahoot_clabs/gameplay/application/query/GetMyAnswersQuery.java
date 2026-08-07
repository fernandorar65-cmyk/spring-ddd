package kahoot.clabs.kahoot_clabs.gameplay.application.query;

import java.util.UUID;

public record GetMyAnswersQuery(UUID organizationId, UUID sessionId, UUID userId) {
}
