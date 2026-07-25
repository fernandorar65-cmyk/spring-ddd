package kahoot.clabs.kahoot_clabs.identity.domain.exception;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException(String email) {
        super("Email is already registered: " + email);
    }
}
