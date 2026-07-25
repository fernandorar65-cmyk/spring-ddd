package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.RoleJpaRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.RolePersistenceMapper;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository jpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Role save(Role role) {
        return RolePersistenceMapper.toDomain(jpaRepository.save(RolePersistenceMapper.toEntity(role)));
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
    }
}
