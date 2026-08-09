package kahoot.clabs.kahoot_clabs.gameplay.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.Category;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    List<Category> findAll();

    List<Category> findByOrganizationId(UUID organizationId);

    void delete(Category category);

    void deleteById(UUID id);
}
