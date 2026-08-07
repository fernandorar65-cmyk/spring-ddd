package kahoot.clabs.kahoot_clabs.identity.application.readmodel;

import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;

public final class PermissionReadModels {

    private PermissionReadModels() {
    }

    public static PermissionReadModel from(Permission permission) {
        return new PermissionReadModel(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.getModule(),
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }
}
