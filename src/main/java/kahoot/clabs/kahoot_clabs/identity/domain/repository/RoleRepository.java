package kahoot.clabs.kahoot_clabs.identity.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    Optional<Role> findByType(RoleType type);

    void delete(Role role);
}
