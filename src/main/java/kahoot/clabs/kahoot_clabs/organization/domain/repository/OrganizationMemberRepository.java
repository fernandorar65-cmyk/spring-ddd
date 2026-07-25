package kahoot.clabs.kahoot_clabs.organization.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;

/**
 * Read side of memberships. Writes always go through the Organization aggregate.
 */
public interface OrganizationMemberRepository {

    List<OrganizationMember> findByOrganizationId(UUID organizationId);

    List<OrganizationMember> findByUserId(UUID userId);

    Optional<OrganizationMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}
