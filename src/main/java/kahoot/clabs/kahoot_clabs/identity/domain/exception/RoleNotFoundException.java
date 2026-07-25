package kahoot.clabs.kahoot_clabs.identity.domain.exception;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(UUID roleId) {
        super("Role not found: " + roleId);
    }

    public RoleNotFoundException(RoleType type) {
        super("Role not found: " + type);
    }
}
