package kahoot.clabs.kahoot_clabs.identity.domain.exception;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
