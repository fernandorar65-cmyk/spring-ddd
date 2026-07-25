package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

@Service
public class RemoveMemberUseCase {

    private final OrganizationRepository organizationRepository;

    public RemoveMemberUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationResponse execute(UUID organizationId, UUID userId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        organization.removeMember(userId);
        return OrganizationResponse.from(organizationRepository.save(organization));
    }
}
