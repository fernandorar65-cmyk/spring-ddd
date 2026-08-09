package kahoot.clabs.kahoot_clabs.identity.application.readmodel;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;

public final class RoleReadModels {

    private RoleReadModels() {
    }

    public static RoleReadModel from(Role role) {
        return new RoleReadModel(
                role.getId(),
                role.getName(),
                role.getType().name(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                role.getPermissions().stream().map(Permission::getId).toList());
    }
}
