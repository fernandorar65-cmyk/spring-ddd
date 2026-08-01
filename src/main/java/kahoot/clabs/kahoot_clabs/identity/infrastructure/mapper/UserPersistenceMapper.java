package kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper;

import java.util.ArrayList;
import java.util.List;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.UserImages;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Email;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.FullName;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserProfile;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserStatus;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.UserEntity;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.UserImagesEntity;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setRoleId(user.getRoleId());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPassword().hashedValue());
        entity.setFirstName(user.getFullName().firstName());
        entity.setLastName(user.getFullName().lastName());
        entity.setStatus(user.getStatus().name());

        UserProfile profile = user.getProfile();
        if (profile != null) {
            entity.setPhoneNumber(profile.phoneNumber());
            entity.setBirthDate(profile.birthDate());
            entity.setBio(profile.bio());
            entity.setLocation(profile.location());
        }

        entity.setLastLogin(user.getLastLogin());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        List<UserImagesEntity> imageEntities = new ArrayList<>();
        for (UserImages image : user.getImages()) {
            UserImagesEntity imageEntity = toImageEntity(image, entity);
            imageEntities.add(imageEntity);
        }
        entity.setImages(imageEntities);
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        UserProfile profile = UserProfile.builder()
                .phoneNumber(entity.getPhoneNumber())
                .birthDate(entity.getBirthDate())
                .bio(entity.getBio())
                .location(entity.getLocation())
                .build();

        List<UserImages> images = entity.getImages() == null
                ? List.of()
                : entity.getImages().stream().map(UserPersistenceMapper::toImageDomain).toList();

        return User.rehydrate(
                entity.getId(),
                entity.getRoleId(),
                Email.of(entity.getEmail()),
                FullName.of(entity.getFirstName(), entity.getLastName()),
                Password.fromHashed(entity.getPasswordHash()),
                profile,
                UserStatus.valueOf(entity.getStatus()),
                images,
                entity.getLastLogin(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static UserImagesEntity toImageEntity(UserImages image, UserEntity user) {
        UserImagesEntity entity = new UserImagesEntity();
        entity.setId(image.getId());
        entity.setUser(user);
        entity.setUrl(image.getUrl());
        entity.setType(image.getType());
        entity.setAlt(image.getAlt());
        entity.setSlug(image.getSlug());
        entity.setCreatedAt(image.getCreatedAt());
        entity.setUpdatedAt(image.getUpdatedAt());
        return entity;
    }

    private static UserImages toImageDomain(UserImagesEntity entity) {
        return UserImages.rehydrate(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUrl(),
                entity.getType(),
                entity.getAlt(),
                entity.getSlug(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
