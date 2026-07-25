package kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject;

import java.util.Objects;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public final class PlayerScore {

    private final int value;

    private PlayerScore(int value) {
        if (value < 0) {
            throw new DomainException("Player score cannot be negative");
        }
        this.value = value;
    }

    public static PlayerScore zero() {
        return new PlayerScore(0);
    }

    public static PlayerScore of(int value) {
        return new PlayerScore(value);
    }

    public PlayerScore plus(int points) {
        if (points < 0) {
            throw new DomainException("Awarded points cannot be negative");
        }
        return new PlayerScore(value + points);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlayerScore that)) {
            return false;
        }
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
