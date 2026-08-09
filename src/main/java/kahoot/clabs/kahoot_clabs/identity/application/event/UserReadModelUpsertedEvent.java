package kahoot.clabs.kahoot_clabs.identity.application.event;

import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;

/** Application event: user write-side change ready to project to the read model. */
public record UserReadModelUpsertedEvent(UserReadModel readModel) {
}
