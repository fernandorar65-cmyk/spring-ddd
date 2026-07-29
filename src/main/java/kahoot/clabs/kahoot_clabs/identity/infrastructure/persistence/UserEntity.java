package kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(length = 36, nullable = false)
    private UUID id;

    @Column(name = "role_id", length = 36)
    private UUID roleId;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    // @Column(nullable = false, length = 20)
    // private String status;

    @Column(length = 500)
    private String avatar;

    // @Column(length = 100)
    // private String department;

    // @Column(name = "job_title", length = 100)
    // private String jobTitle;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 150)
    private String location;

    @Column(name = "profile_avatar_url", length = 500)
    private String profileAvatarUrl;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // public UUID getId() {
    //     return id;
    // }

    // public void setId(UUID id) {
    //     this.id = id;
    // }

    // public UUID getRoleId() {
    //     return roleId;
    // }

    // public void setRoleId(UUID roleId) {
    //     this.roleId = roleId;
    // }

    // public String getEmail() {
    //     return email;
    // }

    // public void setEmail(String email) {
    //     this.email = email;
    // }

    // public String getPasswordHash() {
    //     return passwordHash;
    // }

    // public void setPasswordHash(String passwordHash) {
    //     this.passwordHash = passwordHash;
    // }

    // public String getFirstName() {
    //     return firstName;
    // }

    // public void setFirstName(String firstName) {
    //     this.firstName = firstName;
    // }

    // public String getLastName() {
    //     return lastName;
    // }

    // public void setLastName(String lastName) {
    //     this.lastName = lastName;
    // }

    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }

    // public String getAvatar() {
    //     return avatar;
    // }

    // public void setAvatar(String avatar) {
    //     this.avatar = avatar;
    // }

    // public String getDepartment() {
    //     return department;
    // }

    // public void setDepartment(String department) {
    //     this.department = department;
    // }

    // public String getJobTitle() {
    //     return jobTitle;
    // }

    // public void setJobTitle(String jobTitle) {
    //     this.jobTitle = jobTitle;
    // }

    // public String getPhoneNumber() {
    //     return phoneNumber;
    // }

    // public void setPhoneNumber(String phoneNumber) {
    //     this.phoneNumber = phoneNumber;
    // }

    // public LocalDate getBirthDate() {
    //     return birthDate;
    // }

    // public void setBirthDate(LocalDate birthDate) {
    //     this.birthDate = birthDate;
    // }

    // public String getBio() {
    //     return bio;
    // }

    // public void setBio(String bio) {
    //     this.bio = bio;
    // }

    // public String getLocation() {
    //     return location;
    // }

    // public void setLocation(String location) {
    //     this.location = location;
    // }

    // public String getProfileAvatarUrl() {
    //     return profileAvatarUrl;
    // }

    // public void setProfileAvatarUrl(String profileAvatarUrl) {
    //     this.profileAvatarUrl = profileAvatarUrl;
    // }

    // public LocalDateTime getLastLogin() {
    //     return lastLogin;
    // }

    // public void setLastLogin(LocalDateTime lastLogin) {
    //     this.lastLogin = lastLogin;
    // }

    // public LocalDateTime getCreatedAt() {
    //     return createdAt;
    // }

    // public void setCreatedAt(LocalDateTime createdAt) {
    //     this.createdAt = createdAt;
    // }

    // public LocalDateTime getUpdatedAt() {
    //     return updatedAt;
    // }

    // public void setUpdatedAt(LocalDateTime updatedAt) {
    //     this.updatedAt = updatedAt;
    // }
}
