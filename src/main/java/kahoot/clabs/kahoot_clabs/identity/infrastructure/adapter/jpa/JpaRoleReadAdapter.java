package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.port.RoleProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.port.RoleReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.PermissionReadModel;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.PermissionReadModels;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.RoleReadModel;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.RoleReadModels;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.PermissionPersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.RolePersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.PermissionJpaRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.RoleJpaRepository;

@Repository
@Profile("test")
public class JpaRoleReadAdapter implements RoleReadPort, RoleProjectionPort {

    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    public JpaRoleReadAdapter(
            RoleJpaRepository roleJpaRepository,
            PermissionJpaRepository permissionJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Optional<RoleReadModel> findById(UUID id) {
        return roleJpaRepository.findById(id)
                .map(RolePersistenceMapper::toDomain)
                .map(RoleReadModels::from);
    }

    @Override
    public Optional<RoleReadModel> findByType(String type) {
        return roleJpaRepository.findByType(type)
                .map(RolePersistenceMapper::toDomain)
                .map(RoleReadModels::from);
    }

    @Override
    public List<PermissionReadModel> findPermissionsByRoleId(UUID roleId) {
        return permissionJpaRepository.findByRolesId(roleId).stream()
                .map(PermissionPersistenceMapper::toDomain)
                .map(PermissionReadModels::from)
                .toList();
    }

    @Override
    public void saveRole(RoleReadModel readModel) {
        // no-op
    }

    @Override
    public void deleteRoleById(UUID id) {
        // no-op
    }

    @Override
    public void savePermission(PermissionReadModel readModel) {
        // no-op
    }

    @Override
    public void deletePermissionById(UUID id) {
        // no-op
    }
}
