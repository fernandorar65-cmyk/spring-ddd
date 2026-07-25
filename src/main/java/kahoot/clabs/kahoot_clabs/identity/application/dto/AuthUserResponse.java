package kahoot.clabs.kahoot_clabs.identity.application.dto;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;

public record AuthUserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName
) {

    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName());
    }
}
