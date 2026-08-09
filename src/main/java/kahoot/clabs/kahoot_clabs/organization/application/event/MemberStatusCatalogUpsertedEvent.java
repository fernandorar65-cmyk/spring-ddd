package kahoot.clabs.kahoot_clabs.organization.application.event;

import java.util.UUID;

public record MemberStatusCatalogUpsertedEvent(UUID id, String name, String description) {
}
