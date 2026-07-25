package kahoot.clabs.kahoot_clabs.identity.domain.exception;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UUID userId) {
        super("User not found: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found: " + email);
    }
}
