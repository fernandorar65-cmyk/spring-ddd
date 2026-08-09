package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationMemberEntity;

/**
 * Write-side member persistence for the Organization aggregate.
 * Query finds (by user, by org+user) live in Mongo.
 */
public interface OrganizationMemberJpaRepository extends JpaRepository<OrganizationMemberEntity, UUID> {

    /** Load members when rehydrating the aggregate for commands. */
    List<OrganizationMemberEntity> findByOrganizationId(UUID organizationId);

    void deleteByOrganizationId(UUID organizationId);

    void deleteByOrganizationIdAndIdNotIn(UUID organizationId, Collection<UUID> ids);
}
