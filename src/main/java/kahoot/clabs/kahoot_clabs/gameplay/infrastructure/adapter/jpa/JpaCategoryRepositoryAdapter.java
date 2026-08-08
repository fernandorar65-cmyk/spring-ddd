package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.CategoryProjectionPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.CategoryReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.Category;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper.CategoryMapper;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa.SpringCategoryJpaRepository;

@Repository
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final SpringCategoryJpaRepository springDataRepository;
    private final ObjectProvider<CategoryProjectionPort> categoryProjectionPort;

    public JpaCategoryRepositoryAdapter(
            SpringCategoryJpaRepository springDataRepository,
            ObjectProvider<CategoryProjectionPort> categoryProjectionPort) {
        this.springDataRepository = springDataRepository;
        this.categoryProjectionPort = categoryProjectionPort;
    }

    @Override
    public Category save(Category category) {
        Category saved = CategoryMapper.toDomain(springDataRepository.save(CategoryMapper.toEntity(category)));
        categoryProjectionPort.ifAvailable(port -> port.save(CategoryReadModels.from(saved)));
        return saved;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return springDataRepository.findById(id).map(CategoryMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return springDataRepository.findAll().stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findByOrganizationId(UUID organizationId) {
        return springDataRepository.findByOrganizationId(organizationId).stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Category category) {
        springDataRepository.deleteById(category.getId());
        categoryProjectionPort.ifAvailable(port -> port.deleteById(category.getId()));
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
        categoryProjectionPort.ifAvailable(port -> port.deleteById(id));
    }
}
