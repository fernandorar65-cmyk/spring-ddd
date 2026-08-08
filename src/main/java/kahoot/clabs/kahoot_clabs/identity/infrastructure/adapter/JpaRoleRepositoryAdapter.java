package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.port.RoleProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.RoleReadModels;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.RolePersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.RoleJpaRepository;

@Repository
public class JpaRoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository jpaRepository;
    private final ObjectProvider<RoleProjectionPort> roleProjectionPort;

    public JpaRoleRepositoryAdapter(
            RoleJpaRepository jpaRepository,
            ObjectProvider<RoleProjectionPort> roleProjectionPort) {
        this.jpaRepository = jpaRepository;
        this.roleProjectionPort = roleProjectionPort;
    }

    @Override
    public Role save(Role role) {
        Role saved = RolePersistenceMapper.toDomain(jpaRepository.save(RolePersistenceMapper.toEntity(role)));
        // roleProjectionPort.ifAvailable(port -> port.saveRole(RoleReadModels.from(saved)));
        return saved;
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRepository.findById(id).map(RolePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Role> findByType(RoleType type) {
        return jpaRepository.findByType(type.name()).map(RolePersistenceMapper::toDomain);
    }

    @Override
    public void delete(Role role) {
        jpaRepository.deleteById(role.getId());
        roleProjectionPort.ifAvailable(port -> port.deleteRoleById(role.getId()));
    }
}
