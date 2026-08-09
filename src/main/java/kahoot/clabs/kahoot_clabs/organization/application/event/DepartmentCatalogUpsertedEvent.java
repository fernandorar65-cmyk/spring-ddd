package kahoot.clabs.kahoot_clabs.organization.application.event;

import java.util.UUID;

public record DepartmentCatalogUpsertedEvent(UUID id, String name, String description) {
}
