package kahoot.clabs.kahoot_clabs.organization.application.port.integration;

import java.util.Optional;
import java.util.UUID;

/**
 * Anti-corruption / integration port for identity lookups required by organization.
 * Organization must not depend on User/Role aggregates or identity write repositories.
 */
public interface UserDirectoryPort {

    Optional<UUID> findUserIdByEmail(String email);

    Optional<UUID> findRoleIdByType(String roleType);
}
