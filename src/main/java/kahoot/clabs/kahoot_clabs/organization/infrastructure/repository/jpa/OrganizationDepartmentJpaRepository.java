package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationDepartmentEntity;

/**
 * Write-side department catalog. Finds by name live in Mongo.
 */
public interface OrganizationDepartmentJpaRepository extends JpaRepository<OrganizationDepartmentEntity, UUID> {

    boolean existsByName(String name);
}
