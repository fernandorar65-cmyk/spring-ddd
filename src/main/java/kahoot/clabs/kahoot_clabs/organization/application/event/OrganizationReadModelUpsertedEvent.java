package kahoot.clabs.kahoot_clabs.organization.application.event;

import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModel;

public record OrganizationReadModelUpsertedEvent(OrganizationReadModel readModel) {
}
