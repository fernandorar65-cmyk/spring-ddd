package kahoot.clabs.kahoot_clabs.users.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.users.domain.enums.RoleType;
import kahoot.clabs.kahoot_clabs.users.domain.model.Role;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    Optional<Role> findByType(RoleType type);

    void delete(Role role);
}
