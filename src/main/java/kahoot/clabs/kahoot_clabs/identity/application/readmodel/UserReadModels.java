package kahoot.clabs.kahoot_clabs.identity.application.readmodel;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.UserImages;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserProfile;

public final class UserReadModels {

    private UserReadModels() {
    }

    public static UserReadModel from(User user) {
        UserProfile profile = user.getProfile();
        return new UserReadModel(
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
                user.getLastLogin(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getImages().stream().map(UserReadModels::fromImage).toList());
    }

    private static UserReadModel.ImageReadModel fromImage(UserImages image) {
        return new UserReadModel.ImageReadModel(
                image.getId(),
                image.getUserId(),
                image.getUrl(),
                image.getType(),
                image.getAlt(),
                image.getSlug(),
                image.getCreatedAt(),
                image.getUpdatedAt());
    }
}
