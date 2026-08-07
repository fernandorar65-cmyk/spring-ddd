package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.CategoryEntity;

public interface SpringCategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByOrganizationId(UUID organizationId);
}
