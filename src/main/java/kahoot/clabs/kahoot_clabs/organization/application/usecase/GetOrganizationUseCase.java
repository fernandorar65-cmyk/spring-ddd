package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.application.query.GetOrganizationQuery;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

@Service
public class GetOrganizationUseCase {

    private final OrganizationRepository organizationRepository;

    public GetOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public OrganizationResponse execute(GetOrganizationQuery query) {
        Organization organization = organizationRepository.findById(query.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(query.organizationId()));
        return OrganizationResponse.from(organization);
    }
}
