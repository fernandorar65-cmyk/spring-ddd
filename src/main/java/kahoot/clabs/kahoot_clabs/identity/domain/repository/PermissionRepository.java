package kahoot.clabs.kahoot_clabs.identity.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;

public interface PermissionRepository {

    Permission save(Permission permission);

    Optional<Permission> findById(UUID id);

    Optional<Permission> findByNameAndModule(String name, String module);

    List<Permission> findAll();
}
