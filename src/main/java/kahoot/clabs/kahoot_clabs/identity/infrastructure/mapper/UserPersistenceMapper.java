package kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper;

import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserStatus;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Email;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.FullName;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserProfile;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.UserEntity;

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
        entity.setAvatar(user.getAvatar());
        UserProfile profile = user.getProfile();
        if (profile != null) {
            entity.setDepartment(profile.department());
            entity.setJobTitle(profile.jobTitle());
            entity.setPhoneNumber(profile.phoneNumber());
            entity.setBirthDate(profile.birthDate());
            entity.setBio(profile.bio());
            entity.setLocation(profile.location());
            entity.setProfileAvatarUrl(profile.avatarUrl());
        }
        entity.setLastLogin(user.getLastLogin());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        UserProfile profile = UserProfile.builder()
                .department(entity.getDepartment())
                .jobTitle(entity.getJobTitle())
                .phoneNumber(entity.getPhoneNumber())
                .birthDate(entity.getBirthDate())
                .bio(entity.getBio())
                .location(entity.getLocation())
                .avatarUrl(entity.getProfileAvatarUrl())
                .build();

        return User.rehydrate(
                entity.getId(),
                entity.getRoleId(),
                Email.of(entity.getEmail()),
                FullName.of(entity.getFirstName(), entity.getLastName()),
                Password.fromHashed(entity.getPasswordHash()),
                profile,
                UserStatus.valueOf(entity.getStatus()),
                entity.getAvatar(),
                entity.getLastLogin(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
