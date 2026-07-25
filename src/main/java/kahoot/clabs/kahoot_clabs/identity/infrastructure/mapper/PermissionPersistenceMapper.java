package kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper;

import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.PermissionEntity;

public final class PermissionPersistenceMapper {

    private PermissionPersistenceMapper() {
    }

    public static PermissionEntity toEntity(Permission permission) {
        PermissionEntity entity = new PermissionEntity();
        entity.setId(permission.getId());
        entity.setName(permission.getName());
        entity.setDescription(permission.getDescription());
        entity.setModule(permission.getModule());
        return entity;
    }

    public static Permission toDomain(PermissionEntity entity) {
        return Permission.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getModule());
    }
}
