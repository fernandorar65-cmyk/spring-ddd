package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationMemberStatusCatalogEntity;

/**
 * Write-side member status catalog. Finds by name live in Mongo.
 */
public interface OrganizationMemberStatusCatalogJpaRepository
        extends JpaRepository<OrganizationMemberStatusCatalogEntity, UUID> {

    boolean existsByName(String name);
}
