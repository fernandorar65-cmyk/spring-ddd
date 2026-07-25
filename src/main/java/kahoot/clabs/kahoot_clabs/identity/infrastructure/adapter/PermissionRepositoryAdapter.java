package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.PermissionRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.PermissionPersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.PermissionJpaRepository;

@Repository
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository jpaRepository;

    public PermissionRepositoryAdapter(PermissionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Permission save(Permission permission) {
        return PermissionPersistenceMapper.toDomain(
                jpaRepository.save(PermissionPersistenceMapper.toEntity(permission)));
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaRepository.findById(id).map(PermissionPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByNameAndModule(String name, String module) {
        return jpaRepository.findByNameIgnoreCaseAndModuleIgnoreCase(name, module)
                .map(PermissionPersistenceMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return jpaRepository.findAll().stream()
                .map(PermissionPersistenceMapper::toDomain)
                .toList();
    }
}
