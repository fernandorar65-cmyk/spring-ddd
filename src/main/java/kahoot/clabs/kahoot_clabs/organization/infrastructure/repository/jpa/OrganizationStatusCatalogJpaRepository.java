package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationStatusCatalogEntity;

/**
 * Write-side organization status catalog. Finds by name live in Mongo.
 */
public interface OrganizationStatusCatalogJpaRepository
        extends JpaRepository<OrganizationStatusCatalogEntity, UUID> {

    boolean existsByName(String name);
}
