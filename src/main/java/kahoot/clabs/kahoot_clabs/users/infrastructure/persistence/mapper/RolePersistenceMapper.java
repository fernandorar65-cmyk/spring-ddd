package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

import kahoot.clabs.kahoot_clabs.users.domain.enums.RoleType;
import kahoot.clabs.kahoot_clabs.users.domain.model.Permission;
import kahoot.clabs.kahoot_clabs.users.domain.model.Role;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.entity.PermissionEntity;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.entity.RoleEntity;

public final class RolePersistenceMapper {

    private RolePersistenceMapper() {
    }

    public static RoleEntity toEntity(Role role) {
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        entity.setType(role.getType().name());
        entity.setDescription(role.getDescription());
        entity.setPermissions(role.getPermissions().stream()
                .map(RolePersistenceMapper::toPermissionEntity)
                .collect(Collectors.toSet()));
        return entity;
    }

    public static Role toDomain(RoleEntity entity) {
        List<Permission> permissions = entity.getPermissions().stream()
                .map(RolePersistenceMapper::toPermissionDomain)
                .toList();
        return Role.rehydrate(
                entity.getId(),
                entity.getName(),
                RoleType.valueOf(entity.getType()),
                entity.getDescription(),
                permissions);
    }

    private static PermissionEntity toPermissionEntity(Permission permission) {
        PermissionEntity entity = new PermissionEntity();
        entity.setId(permission.getId());
        entity.setName(permission.getName());
        entity.setDescription(permission.getDescription());
        entity.setModule(permission.getModule());
        return entity;
    }

    private static Permission toPermissionDomain(PermissionEntity entity) {
        return Permission.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getModule());
    }
}
