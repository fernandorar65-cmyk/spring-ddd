package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.PermissionEntity;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, UUID> {

    Optional<PermissionEntity> findByNameIgnoreCaseAndModuleIgnoreCase(String name, String module);
}
