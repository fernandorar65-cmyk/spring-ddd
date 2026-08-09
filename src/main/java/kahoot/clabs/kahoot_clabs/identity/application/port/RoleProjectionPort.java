package kahoot.clabs.kahoot_clabs.identity.application.port;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.application.readmodel.PermissionReadModel;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.RoleReadModel;

public interface RoleProjectionPort {

    void saveRole(RoleReadModel readModel);

    void deleteRoleById(UUID id);

    void savePermission(PermissionReadModel readModel);

    void deletePermissionById(UUID id);
}
