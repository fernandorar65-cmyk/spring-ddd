package kahoot.clabs.kahoot_clabs.gameplay.domain.exception;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class GameSessionNotFoundException extends DomainException {

    public GameSessionNotFoundException(UUID sessionId) {
        super("Game session not found: " + sessionId);
    }
}
