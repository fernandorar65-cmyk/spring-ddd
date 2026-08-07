package kahoot.clabs.kahoot_clabs.quiz.application.readmodel;

import java.util.UUID;

public record CategoryReadModel(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        String color,
        String icon) {
}
