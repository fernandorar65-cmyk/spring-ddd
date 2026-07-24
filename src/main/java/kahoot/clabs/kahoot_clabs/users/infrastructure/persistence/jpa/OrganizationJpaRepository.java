package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.entity.OrganizationEntity;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, UUID> {
    Optional<OrganizationEntity> findBySlug(String slug);
}
