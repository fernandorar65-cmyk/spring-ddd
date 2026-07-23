package kahoot.clabs.kahoot_clabs.quizes.domain.model.ValueObjects;
import java.time.Duration;

public final class EstimatedTime {

    private final Duration value;

    private EstimatedTime(Duration value) {

        if (value.isNegative() || value.isZero()) {
            throw new IllegalArgumentException("Time must be greater than zero.");
        }

        if (value.toMinutes() > 180) {
            throw new IllegalArgumentException("Maximum time is 180 minutes.");
        }

        this.value = value;
    }

    public static EstimatedTime ofMinutes(long minutes) {
        return new EstimatedTime(Duration.ofMinutes(minutes));
    }

    public Duration value() {
        return value;
    }

    public long toMinutes() {
        return value.toMinutes();
    }
}