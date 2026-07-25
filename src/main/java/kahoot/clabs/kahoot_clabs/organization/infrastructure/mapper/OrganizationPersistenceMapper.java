package kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper;

import java.util.List;

import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationStatus;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationEntity;

public final class OrganizationPersistenceMapper {

    private OrganizationPersistenceMapper() {
    }

    public static OrganizationEntity toEntity(Organization organization) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(organization.getId());
        entity.setName(organization.getName().value());
        entity.setSlug(organization.getSlug().value());
        entity.setLogo(organization.getLogo());
        entity.setDescription(organization.getDescription());
        entity.setTimezone(organization.getTimezone());
        entity.setLanguage(organization.getLanguage());
        entity.setStatus(organization.getStatus().name());
        entity.setCreatedAt(organization.getCreatedAt());
        entity.setUpdatedAt(organization.getUpdatedAt());
        return entity;
    }

    public static Organization toDomain(OrganizationEntity entity, List<OrganizationMember> members) {
        return Organization.rehydrate(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getLogo(),
                entity.getDescription(),
                entity.getTimezone(),
                entity.getLanguage(),
                OrganizationStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                members);
    }
}
