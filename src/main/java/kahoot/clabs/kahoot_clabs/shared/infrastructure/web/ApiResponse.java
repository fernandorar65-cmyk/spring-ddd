package kahoot.clabs.kahoot_clabs.shared.infrastructure.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data,
        Map<String, String> errors) {

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status.value(), message, data, Map.of());
    }

    public static ApiResponse<Void> error(HttpStatus status, String message) {
        return new ApiResponse<>(Instant.now(), status.value(), message, null, Map.of());
    }

    public static ApiResponse<Void> validation(Map<String, String> errors) {
        return new ApiResponse<>(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                null,
                Map.copyOf(errors));
    }
}
