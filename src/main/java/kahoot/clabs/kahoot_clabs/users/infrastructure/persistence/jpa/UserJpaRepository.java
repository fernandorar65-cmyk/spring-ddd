package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
