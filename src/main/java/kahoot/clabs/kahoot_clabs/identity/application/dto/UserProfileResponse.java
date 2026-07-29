package kahoot.clabs.kahoot_clabs.identity.application.dto;

import java.time.LocalDate;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserProfile;

public record UserProfileResponse(
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
        String profileImageUrl
) {

    public static UserProfileResponse from(User user) {
        UserProfile profile = user.getProfile();
        return new UserProfileResponse(
                user.getId(),
                user.getRoleId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName(),
                user.getStatus().name(),
                profile.phoneNumber(),
                profile.birthDate(),
                profile.bio(),
                profile.location(),
                user.profileImageUrl().orElse(null));
    }
}
