package kahoot.clabs.kahoot_clabs.organization.domain.exception;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class OrganizationSlugAlreadyTakenException extends DomainException {

    public OrganizationSlugAlreadyTakenException(String slug) {
        super("Organization slug is already taken: " + slug);
    }
}
