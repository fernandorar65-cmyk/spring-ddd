package kahoot.clabs.kahoot_clabs.identity.application.dto;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;

public record UserRoleResponse(
        String name,
        String description
) {

    public static UserRoleResponse from(Permission permission) {
        return new UserRoleResponse(
                permission.getName(),
                permission.getDescription());
    }
}
