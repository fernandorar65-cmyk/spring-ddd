package kahoot.clabs.kahoot_clabs.identity.application.port;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;

public interface UserProjectionPort {

    void save(UserReadModel readModel);

    void deleteById(UUID id);
}
