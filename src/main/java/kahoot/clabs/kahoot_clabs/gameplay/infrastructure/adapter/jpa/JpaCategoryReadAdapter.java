package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.CategoryProjectionPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.CategoryReadPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.CategoryReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.CategoryReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper.CategoryMapper;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa.SpringCategoryJpaRepository;


@Repository
@Profile("test")
public class JpaCategoryReadAdapter implements CategoryReadPort, CategoryProjectionPort {

    private final SpringCategoryJpaRepository categoryJpaRepository;

    public JpaCategoryReadAdapter(SpringCategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Optional<CategoryReadModel> findById(UUID id) {
        return categoryJpaRepository.findById(id)
                .map(CategoryMapper::toDomain)
                .map(CategoryReadModels::from);
    }

    @Override
    public List<CategoryReadModel> findByOrganizationId(UUID organizationId) {
        return categoryJpaRepository.findByOrganizationId(organizationId).stream()
                .map(CategoryMapper::toDomain)
                .map(CategoryReadModels::from)
                .toList();
    }

    @Override
    public void save(CategoryReadModel readModel) {
        // no-op
    }

    @Override
    public void deleteById(UUID id) {
        // no-op
    }
}
