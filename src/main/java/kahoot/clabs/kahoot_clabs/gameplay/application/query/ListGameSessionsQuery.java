package kahoot.clabs.kahoot_clabs.gameplay.application.query;

import java.util.UUID;

public record ListGameSessionsQuery(UUID organizationId, String statusCsv, UUID quizId) {
}
