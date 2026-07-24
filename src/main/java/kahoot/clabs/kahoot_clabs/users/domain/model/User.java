package kahoot.clabs.kahoot_clabs.users.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.AggregateRoot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.users.domain.enums.UserStatus;
import kahoot.clabs.kahoot_clabs.users.domain.event.UserCreatedEvent;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueobject.Email;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueobject.FullName;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueobject.Password;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueobject.UserProfile;

public class User extends AggregateRoot {

    private final UUID id;
    private final UUID organizationId;
    private UUID roleId;

    private FullName fullName;
    private Email email;
    private Password password;
    private UserProfile profile;

    private UserStatus status;
    private String avatar;

    private LocalDateTime lastLogin;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(UUID id, UUID organizationId, Email email, FullName fullName, Password password,
                 LocalDateTime createdAt) {
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (password == null) {
            throw new DomainException("Password is required");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.organizationId = organizationId;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.profile = UserProfile.empty();
        this.status = UserStatus.ACTIVE;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Creates a new user with an already-hashed password (Application hashes via PasswordHasher).
     */
    public static User create(
            UUID organizationId,
            String email,
            String firstName,
            String lastName,
            Password hashedPassword) {
        User user = new User(
                null,
                organizationId,
                Email.of(email),
                FullName.of(firstName, lastName),
                hashedPassword,
                null);
        user.registerEvent(new UserCreatedEvent(user.id, organizationId, user.email.value()));
        return user;
    }

    public static User rehydrate(
            UUID id,
            UUID organizationId,
            UUID roleId,
            Email email,
            FullName fullName,
            Password password,
            UserProfile profile,
            UserStatus status,
            String avatar,
            LocalDateTime lastLogin,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        User user = new User(id, organizationId, email, fullName, password, createdAt);
        user.roleId = roleId;
        user.profile = profile != null ? profile : UserProfile.empty();
        user.status = status != null ? status : UserStatus.ACTIVE;
        user.avatar = avatar;
        user.lastLogin = lastLogin;
        user.updatedAt = updatedAt != null ? updatedAt : user.createdAt;
        return user;
    }

    public void changeEmail(String email) {
        this.email = Email.of(email);
        touch();
    }

    public void changeFullName(String firstName, String lastName) {
        this.fullName = FullName.of(firstName, lastName);
        touch();
    }

    public void changePassword(Password password) {
        if (password == null) {
            throw new DomainException("Password is required");
        }
        this.password = password;
        touch();
    }

    public void updateProfile(UserProfile profile) {
        if (profile == null) {
            throw new DomainException("Profile is required");
        }
        this.profile = profile;
        touch();
    }

    public void changeRole(UUID roleId) {
        if (roleId == null) {
            throw new DomainException("Role id is required");
        }
        this.roleId = roleId;
        touch();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        touch();
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        touch();
    }

    public void recordLogin() {
        if (status != UserStatus.ACTIVE) {
            throw new DomainException("Only active users can log in");
        }
        this.lastLogin = LocalDateTime.now();
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public FullName getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getAvatar() {
        return avatar;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
