package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.RoleEntity;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByType(String type);
}
