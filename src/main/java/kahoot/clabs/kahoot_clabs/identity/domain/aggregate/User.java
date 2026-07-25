package kahoot.clabs.kahoot_clabs.identity.domain.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.event.UserCreatedEvent;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Email;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.FullName;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserProfile;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.AggregateRoot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Identity of a person in the platform. This aggregate knows nothing about organizations:
 * membership lives in the organization bounded context.
 */
public class User extends AggregateRoot {

    private UUID roleId;

    private FullName fullName;
    private Email email;
    private Password password;
    private UserProfile profile;

    private UserStatus status;
    private String avatar;

    private LocalDateTime lastLogin;

    private User(UUID id, Email email, FullName fullName, Password password, LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (password == null) {
            throw new DomainException("Password is required");
        }
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.profile = UserProfile.empty();
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Creates a new user with an already-hashed password (Application hashes via PasswordHasher).
     */
    public static User create(
            String email,
            String firstName,
            String lastName,
            Password hashedPassword) {
        User user = new User(
                null,
                Email.of(email),
                FullName.of(firstName, lastName),
                hashedPassword,
                null,
                null);
        user.registerEvent(new UserCreatedEvent(user.getId(), user.email.value()));
        return user;
    }

    public static User rehydrate(
            UUID id,
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
        User user = new User(id, email, fullName, password, createdAt, updatedAt);
        user.roleId = roleId;
        user.profile = profile != null ? profile : UserProfile.empty();
        user.status = status != null ? status : UserStatus.ACTIVE;
        user.avatar = avatar;
        user.lastLogin = lastLogin;
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

    public void changeAvatar(String avatar) {
        this.avatar = avatar;
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
}
