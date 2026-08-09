package kahoot.clabs.kahoot_clabs.identity.application.readmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserReadModel(
        UUID id,
        UUID roleId,
        String email,
        String firstName,
        String lastName,
        String status,
        String phoneNumber,
        LocalDate birthDate,
        String bio,
        String location,
        LocalDateTime lastLogin,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ImageReadModel> images) {

    public UserReadModel {
        images = images == null ? List.of() : List.copyOf(images);
    }

    public record ImageReadModel(
            UUID id,
            UUID userId,
            String url,
            String type,
            String alt,
            String slug,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }
}
