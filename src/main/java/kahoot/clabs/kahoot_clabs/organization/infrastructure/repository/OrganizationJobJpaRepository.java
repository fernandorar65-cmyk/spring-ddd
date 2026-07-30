package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationJobEntity;

public interface OrganizationJobJpaRepository extends JpaRepository<OrganizationJobEntity, UUID> {

    Optional<OrganizationJobEntity> findByName(String name);

    boolean existsByName(String name);
}
