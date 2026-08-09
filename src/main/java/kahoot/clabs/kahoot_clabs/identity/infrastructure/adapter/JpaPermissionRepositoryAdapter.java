package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.event.PermissionReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.PermissionReadModels;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.PermissionRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.mapper.PermissionPersistenceMapper;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.jpa.PermissionJpaRepository;

@Repository
public class JpaPermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JpaPermissionRepositoryAdapter(
            PermissionJpaRepository jpaRepository,
            ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Permission save(Permission permission) {
        Permission saved = PermissionPersistenceMapper.toDomain(jpaRepository.save(PermissionPersistenceMapper.toEntity(permission)));
        eventPublisher.publishEvent(new PermissionReadModelUpsertedEvent(PermissionReadModels.from(saved)));
        return saved;
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

    @Override
    public List<Permission> findAllByRoleId(UUID roleId) {
        return jpaRepository.findByRolesId(roleId).stream()
                .map(PermissionPersistenceMapper::toDomain)
                .toList();
    }
}
