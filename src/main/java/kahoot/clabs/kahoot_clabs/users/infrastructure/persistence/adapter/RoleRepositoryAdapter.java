package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.users.domain.enums.RoleType;
import kahoot.clabs.kahoot_clabs.users.domain.model.Role;
import kahoot.clabs.kahoot_clabs.users.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.jpa.RoleJpaRepository;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.mapper.RolePersistenceMapper;

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
