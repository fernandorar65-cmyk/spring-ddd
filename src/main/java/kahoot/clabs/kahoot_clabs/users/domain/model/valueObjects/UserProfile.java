package kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class UserProfile {

    private final String department;
    private final String jobTitle;
    private final String phoneNumber;
    private final LocalDate birthDate;
    private final String bio;
    private final String location;
    private final String avatarUrl;        // Si quieres separar del avatar principal

    private UserProfile(Builder builder) {
        this.department = builder.department;
        this.jobTitle = builder.jobTitle;
        this.phoneNumber = builder.phoneNumber;
        this.birthDate = builder.birthDate;
        this.bio = builder.bio;
        this.location = builder.location;
        this.avatarUrl = builder.avatarUrl;
    }

    public static UserProfile empty() {
        return new Builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder pattern para Value Object (más cómodo)
    public static class Builder {
        private String department;
        private String jobTitle;
        private String phoneNumber;
        private LocalDate birthDate;
        private String bio;
        private String location;
        private String avatarUrl;

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder jobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(this);
        }
    }
}