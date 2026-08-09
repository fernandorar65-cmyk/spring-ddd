package kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.CategoryReadModel;

public interface CategoryReadPort {

    Optional<CategoryReadModel> findById(UUID id);

    List<CategoryReadModel> findByOrganizationId(UUID organizationId);
}
