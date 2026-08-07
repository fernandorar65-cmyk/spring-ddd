package kahoot.clabs.kahoot_clabs.gameplay.application.query;

import java.util.UUID;

public record ListSessionQuestionsQuery(UUID organizationId, UUID sessionId, boolean asHost) {
}
