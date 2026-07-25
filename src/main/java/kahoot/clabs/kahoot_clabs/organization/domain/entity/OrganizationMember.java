package kahoot.clabs.kahoot_clabs.organization.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.MemberStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Membership of a user (identity context) inside an organization.
 * Only the Organization aggregate root creates or modifies members.
 */
public class OrganizationMember extends BaseEntity {

    private final UUID organizationId;
    private final UUID userId;
    private UUID roleId;
    private MemberStatus status;
    private final LocalDateTime joinedAt;

    private OrganizationMember(
            UUID id,
            UUID organizationId,
            UUID userId,
            UUID roleId,
            MemberStatus status,
            LocalDateTime joinedAt) {
        super(id);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (userId == null) {
            throw new DomainException("User id is required");
        }
        this.organizationId = organizationId;
        this.userId = userId;
        this.roleId = roleId;
        this.status = status != null ? status : MemberStatus.ACTIVE;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
    }

    public static OrganizationMember active(UUID organizationId, UUID userId, UUID roleId) {
        return new OrganizationMember(null, organizationId, userId, roleId, MemberStatus.ACTIVE, null);
    }

    public static OrganizationMember invited(UUID organizationId, UUID userId, UUID roleId) {
        return new OrganizationMember(null, organizationId, userId, roleId, MemberStatus.INVITED, null);
    }

    public static OrganizationMember rehydrate(
            UUID id,
            UUID organizationId,
            UUID userId,
            UUID roleId,
            MemberStatus status,
            LocalDateTime joinedAt) {
        return new OrganizationMember(id, organizationId, userId, roleId, status, joinedAt);
    }

    public void acceptInvitation() {
        if (status != MemberStatus.INVITED) {
            throw new DomainException("Only invited members can accept an invitation");
        }
        this.status = MemberStatus.ACTIVE;
    }

    public void suspend() {
        this.status = MemberStatus.SUSPENDED;
    }

    public void reactivate() {
        this.status = MemberStatus.ACTIVE;
    }

    public void changeRole(UUID roleId) {
        if (roleId == null) {
            throw new DomainException("Role id is required");
        }
        this.roleId = roleId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
