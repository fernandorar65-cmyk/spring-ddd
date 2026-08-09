package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationEntity;

/**
 * Write-side Spring Data repository. Query finds live in Mongo
 * ({@code OrganizationMongoRepository}).
 */
public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, UUID> {

    /** Aggregate rehydration for commands / seed (write model). */
    Optional<OrganizationEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
