package kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject;

import java.security.SecureRandom;
import java.util.Objects;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Code players type to join a session.
 */
public final class GamePin {

    private static final int LENGTH = 6;
    private static final String PIN_PATTERN = "^[0-9]{" + LENGTH + "}$";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final String value;

    private GamePin(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Game pin is required");
        }
        String normalized = value.trim();
        if (!normalized.matches(PIN_PATTERN)) {
            throw new DomainException("Game pin must have exactly " + LENGTH + " digits");
        }
        this.value = normalized;
    }

    public static GamePin of(String value) {
        return new GamePin(value);
    }

    public static GamePin random() {
        StringBuilder digits = new StringBuilder(LENGTH);
        for (int index = 0; index < LENGTH; index++) {
            digits.append(RANDOM.nextInt(10));
        }
        return new GamePin(digits.toString());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GamePin that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
