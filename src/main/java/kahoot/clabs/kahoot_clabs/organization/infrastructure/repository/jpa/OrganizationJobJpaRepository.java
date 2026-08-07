package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationJobEntity;

/**
 * Write-side job catalog. Finds by name live in Mongo.
 */
public interface OrganizationJobJpaRepository extends JpaRepository<OrganizationJobEntity, UUID> {

    boolean existsByName(String name);
}
