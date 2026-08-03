package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.dto.UserRoleResponse;
import kahoot.clabs.kahoot_clabs.identity.application.query.GetUserRolesQuery;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.PermissionRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;

@Service
public class GetUserRolesUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public GetUserRolesUseCase(
            UserRepository userRepository,
            PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<UserRoleResponse> execute(GetUserRolesQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(query.userId()));
                
        if (user.getRoleId() == null) {
            return Collections.emptyList();
        }

        System.out.println("user.getRoleId(): " + user.getRoleId());
        
        List<Permission> permissions = permissionRepository.findAllByRoleId(user.getRoleId());

        System.out.println(permissions);

        return permissions.stream().map(UserRoleResponse::from).toList();
    }
}
