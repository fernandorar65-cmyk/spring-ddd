package kahoot.clabs.kahoot_clabs.users.domain.model;
import lombok.Getter;
import java.util.UUID;

@Getter
public class RolePermission {

    private final UUID roleId;
    private final UUID permissionId;

    private RolePermission(UUID roleId, UUID permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public static RolePermission create(UUID roleId, UUID permissionId) {
        return new RolePermission(roleId, permissionId);
    }
}