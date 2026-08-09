package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationReadPort;
import kahoot.clabs.kahoot_clabs.organization.application.query.GetOrganizationQuery;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;

@Service
public class GetOrganizationUseCase {

    private final OrganizationReadPort organizationReadPort;

    public GetOrganizationUseCase(OrganizationReadPort organizationReadPort) {
        this.organizationReadPort = organizationReadPort;
    }

    public OrganizationResponse execute(GetOrganizationQuery query) {
        return organizationReadPort.findById(query.organizationId())
                .map(OrganizationResponse::from)
                .orElseThrow(() -> new OrganizationNotFoundException(query.organizationId()));
    }
}
