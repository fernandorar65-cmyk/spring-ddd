package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.identity.application.dto.UserRoleResponse;
import kahoot.clabs.kahoot_clabs.identity.application.port.RoleReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.port.UserReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.query.GetUserRolesQuery;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;

@Service
public class GetUserRolesUseCase {

    private final UserReadPort userReadPort;
    private final RoleReadPort roleReadPort;

    public GetUserRolesUseCase(UserReadPort userReadPort, RoleReadPort roleReadPort) {
        this.userReadPort = userReadPort;
        this.roleReadPort = roleReadPort;
    }

    public List<UserRoleResponse> execute(GetUserRolesQuery query) {
        UserReadModel user = userReadPort.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(query.userId()));

        if (user.roleId() == null) {
            return Collections.emptyList();
        }

        return roleReadPort.findPermissionsByRoleId(user.roleId()).stream()
                .map(permission -> new UserRoleResponse(permission.name(), permission.description()))
                .toList();
    }
}
