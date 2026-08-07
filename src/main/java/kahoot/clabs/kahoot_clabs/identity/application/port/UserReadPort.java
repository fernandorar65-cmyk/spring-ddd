package kahoot.clabs.kahoot_clabs.identity.application.port;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;

public interface UserReadPort {

    Optional<UserReadModel> findById(UUID id);

    Optional<UserReadModel> findByEmail(String email);
}
