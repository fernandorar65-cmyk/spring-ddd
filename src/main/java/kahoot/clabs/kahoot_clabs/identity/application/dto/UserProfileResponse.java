package kahoot.clabs.kahoot_clabs.identity.application.dto;

import java.time.LocalDate;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.UserImages;
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

    public static UserProfileResponse from(UserReadModel readModel) {
        String profileImageUrl = readModel.images().stream()
                .filter(image -> UserImages.TYPE_PROFILE.equalsIgnoreCase(image.type()))
                .map(UserReadModel.ImageReadModel::url)
                .findFirst()
                .orElse(null);
        return new UserProfileResponse(
                readModel.id(),
                readModel.roleId(),
                readModel.email(),
                readModel.firstName(),
                readModel.lastName(),
                readModel.status(),
                readModel.phoneNumber(),
                readModel.birthDate(),
                readModel.bio(),
                readModel.location(),
                profileImageUrl);
    }
}
