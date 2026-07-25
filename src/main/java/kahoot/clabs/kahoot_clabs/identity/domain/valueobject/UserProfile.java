package kahoot.clabs.kahoot_clabs.identity.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

public final class UserProfile {

    private final String department;
    private final String jobTitle;
    private final String phoneNumber;
    private final LocalDate birthDate;
    private final String bio;
    private final String location;
    private final String avatarUrl;

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

    public String department() {
        return department;
    }

    public String jobTitle() {
        return jobTitle;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public String bio() {
        return bio;
    }

    public String location() {
        return location;
    }

    public String avatarUrl() {
        return avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile that)) {
            return false;
        }
        return Objects.equals(department, that.department)
                && Objects.equals(jobTitle, that.jobTitle)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(birthDate, that.birthDate)
                && Objects.equals(bio, that.bio)
                && Objects.equals(location, that.location)
                && Objects.equals(avatarUrl, that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department, jobTitle, phoneNumber, birthDate, bio, location, avatarUrl);
    }

    public static final class Builder {
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
