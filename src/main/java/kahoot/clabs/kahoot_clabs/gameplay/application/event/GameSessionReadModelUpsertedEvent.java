package kahoot.clabs.kahoot_clabs.gameplay.application.event;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;

public record GameSessionReadModelUpsertedEvent(GameSessionReadModel readModel) {
}
