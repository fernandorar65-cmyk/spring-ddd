package kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.RoleEntity;

public final class RolePersistenceMapper {

    private RolePersistenceMapper() {
    }

    public static RoleEntity toEntity(Role role) {
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        entity.setType(role.getType().name());
        entity.setDescription(role.getDescription());
        entity.setCreatedAt(role.getCreatedAt());
        entity.setUpdatedAt(role.getUpdatedAt());
        entity.setPermissions(role.getPermissions().stream()
                .map(PermissionPersistenceMapper::toEntity)
                .collect(Collectors.toSet()));
        return entity;
    }

    public static Role toDomain(RoleEntity entity) {
        List<Permission> permissions = entity.getPermissions().stream()
                .map(PermissionPersistenceMapper::toDomain)
                .toList();
        return Role.rehydrate(
                entity.getId(),
                entity.getName(),
                RoleType.valueOf(entity.getType()),
                entity.getDescription(),
                permissions,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
