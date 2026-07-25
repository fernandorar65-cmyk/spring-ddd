package kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject;

import java.time.Duration;
import java.util.Objects;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * How long a player took to answer a question.
 */
public final class ResponseTime {

    private final Duration value;

    private ResponseTime(Duration value) {
        if (value == null || value.isNegative()) {
            throw new DomainException("Response time cannot be negative");
        }
        this.value = value;
    }

    public static ResponseTime ofMillis(long millis) {
        return new ResponseTime(Duration.ofMillis(millis));
    }

    public Duration value() {
        return value;
    }

    public long toMillis() {
        return value.toMillis();
    }

    public boolean isWithin(int seconds) {
        return value.getSeconds() <= seconds;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResponseTime that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
