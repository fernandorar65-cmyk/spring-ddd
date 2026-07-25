package kahoot.clabs.kahoot_clabs.identity.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    void delete(User user);
}
