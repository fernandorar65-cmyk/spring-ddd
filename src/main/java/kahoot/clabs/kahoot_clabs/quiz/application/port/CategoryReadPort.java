package kahoot.clabs.kahoot_clabs.quiz.application.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.CategoryReadModel;

public interface CategoryReadPort {

    Optional<CategoryReadModel> findById(UUID id);

    List<CategoryReadModel> findByOrganizationId(UUID organizationId);
}
