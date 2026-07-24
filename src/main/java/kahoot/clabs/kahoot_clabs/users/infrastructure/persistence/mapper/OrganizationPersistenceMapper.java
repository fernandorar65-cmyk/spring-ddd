package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.mapper;

import kahoot.clabs.kahoot_clabs.users.domain.enums.OrganizationStatus;
import kahoot.clabs.kahoot_clabs.users.domain.model.Organization;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.entity.OrganizationEntity;

public final class OrganizationPersistenceMapper {

    private OrganizationPersistenceMapper() {
    }

    public static OrganizationEntity toEntity(Organization organization) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(organization.getId());
        entity.setName(organization.getName());
        entity.setSlug(organization.getSlug());
        entity.setLogo(organization.getLogo());
        entity.setDescription(organization.getDescription());
        entity.setTimezone(organization.getTimezone());
        entity.setLanguage(organization.getLanguage());
        entity.setStatus(organization.getStatus().name());
        entity.setCreatedAt(organization.getCreatedAt());
        entity.setUpdatedAt(organization.getUpdatedAt());
        return entity;
    }

    public static Organization toDomain(OrganizationEntity entity) {
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
                entity.getUpdatedAt());
    }
}
