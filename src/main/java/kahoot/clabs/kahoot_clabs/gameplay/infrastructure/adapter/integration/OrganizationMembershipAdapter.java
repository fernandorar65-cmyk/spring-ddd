package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter.integration;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

/**
 * ACL adapter: translates organization write-model lookups into a gameplay-safe contract.
 */
@Component
public class OrganizationMembershipAdapter implements OrganizationMembershipPort {

    private final OrganizationRepository organizationRepository;

    public OrganizationMembershipAdapter(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public boolean organizationExists(UUID organizationId) {
        return organizationRepository.findById(organizationId).isPresent();
    }

    @Override
    public boolean isActiveMember(UUID organizationId, UUID userId) {
        return organizationRepository.findById(organizationId)
                .map(organization -> organization.hasMember(userId))
                .orElse(false);
    }
}
