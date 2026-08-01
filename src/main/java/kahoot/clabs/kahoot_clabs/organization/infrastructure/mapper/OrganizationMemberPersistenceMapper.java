package kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper;

import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.MemberStatus;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationMemberEntity;

public final class OrganizationMemberPersistenceMapper {

    private OrganizationMemberPersistenceMapper() {
    }

    public static OrganizationMemberEntity toEntity(OrganizationMember member) {
        OrganizationMemberEntity entity = new OrganizationMemberEntity();
        entity.setId(member.getId());
        entity.setOrganizationId(member.getOrganizationId());
        entity.setUserId(member.getUserId());
        entity.setRoleId(member.getRoleId());
        entity.setStatus(member.getStatus().name());
        entity.setJoinedAt(member.getJoinedAt());
        entity.setCreatedAt(member.getCreatedAt());
        entity.setUpdatedAt(member.getUpdatedAt());
        return entity;
    }

    public static OrganizationMember toDomain(OrganizationMemberEntity entity) {
        return OrganizationMember.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUserId(),
                entity.getRoleId(),
                MemberStatus.valueOf(entity.getStatus()),
                entity.getJoinedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
