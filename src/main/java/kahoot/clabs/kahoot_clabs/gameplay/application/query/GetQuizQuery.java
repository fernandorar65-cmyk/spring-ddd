package kahoot.clabs.kahoot_clabs.gameplay.application.query;

import java.util.UUID;

public record GetQuizQuery(UUID organizationId, UUID quizId) {
}
