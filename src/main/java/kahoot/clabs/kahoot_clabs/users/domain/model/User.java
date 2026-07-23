package kahoot.clabs.kahoot_clabs.users.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;


import kahoot.clabs.kahoot_clabs.users.domain.model.Enums.UserStatus;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects.Email;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects.FullName;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects.Password;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects.UserProfile;
import lombok.Getter;

@Getter
public class User {

    private final UUID id;
    private UUID organizationId;
    private UUID roleId;

    private FullName fullName;        // Value Object
    private Email email;              // Value Object
    private Password password;          // Debe estar hasheado
    private UserProfile profile;

    private UserStatus status;
    private String avatar;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


   private User(UUID id, UUID organizationId, Email email, FullName fullName, Password password) {
        this.id = id != null ? id : UUID.randomUUID();
        this.organizationId = organizationId;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.profile = UserProfile.empty();
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public static User create(UUID organizationId, String email, String firstName, String lastName, String rawPassword) {
        return new User(null, organizationId, new Email(email), new FullName(firstName, lastName), Password.create(rawPassword));
    }

    public void updateProfile(UserProfile profile) {
        this.profile = profile;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeRole(UUID roleId) {
        this.roleId = roleId;
        this.updatedAt = LocalDateTime.now();
    }
}