package kahoot.clabs.kahoot_clabs.organization.application.event;

import java.util.UUID;

public record JobCatalogUpsertedEvent(UUID id, String name, String description) {
}
