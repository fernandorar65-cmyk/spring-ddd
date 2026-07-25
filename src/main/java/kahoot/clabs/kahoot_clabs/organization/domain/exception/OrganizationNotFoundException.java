package kahoot.clabs.kahoot_clabs.organization.domain.exception;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class OrganizationNotFoundException extends DomainException {

    public OrganizationNotFoundException(UUID organizationId) {
        super("Organization not found: " + organizationId);
    }

    public OrganizationNotFoundException(String slug) {
        super("Organization not found: " + slug);
    }
}
