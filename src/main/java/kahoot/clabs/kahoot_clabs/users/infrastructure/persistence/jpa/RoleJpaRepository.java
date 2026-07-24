package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.entity.RoleEntity;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByType(String type);
}
