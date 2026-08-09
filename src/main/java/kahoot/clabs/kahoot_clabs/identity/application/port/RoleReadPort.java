package kahoot.clabs.kahoot_clabs.identity.application.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.application.readmodel.PermissionReadModel;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.RoleReadModel;

public interface RoleReadPort {

    Optional<RoleReadModel> findById(UUID id);

    Optional<RoleReadModel> findByType(String type);

    List<PermissionReadModel> findPermissionsByRoleId(UUID roleId);
}
