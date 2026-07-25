package kahoot.clabs.kahoot_clabs.gameplay.domain.exception;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class GameSessionNotFoundException extends DomainException {

    public GameSessionNotFoundException(UUID gameSessionId) {
        super("Game session not found: " + gameSessionId);
    }

    public GameSessionNotFoundException(String pin) {
        super("Game session not found for pin: " + pin);
    }
}
