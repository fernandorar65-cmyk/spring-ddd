package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter.integration;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.organization.application.port.integration.UserDirectoryPort;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * ACL adapter: translates identity write-model lookups into an organization-safe contract.
 */
@Component
public class UserDirectoryAdapter implements UserDirectoryPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDirectoryAdapter(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<UUID> findUserIdByEmail(String email) {
        return userRepository.findByEmail(email).map(user -> user.getId());
    }

    @Override
    public Optional<UUID> findRoleIdByType(String roleType) {
        RoleType type;
        try {
            type = RoleType.valueOf(roleType);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new DomainException("Invalid role type: " + roleType);
        }
        return roleRepository.findByType(type).map(role -> role.getId());
    }
}
