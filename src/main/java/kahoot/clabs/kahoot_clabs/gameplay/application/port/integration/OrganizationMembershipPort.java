package kahoot.clabs.kahoot_clabs.gameplay.application.port.integration;

import java.util.UUID;

/**
 * Anti-corruption / integration port for organization membership checks required by gameplay.
 * Gameplay must not depend on Organization aggregates or write repositories.
 */
public interface OrganizationMembershipPort {

    boolean organizationExists(UUID organizationId);

    /**
     * Whether the user is associated with the organization (any non-absent membership).
     * Matches previous {@code Organization.hasMember} semantics used by gameplay.
     */
    boolean isActiveMember(UUID organizationId, UUID userId);
}
