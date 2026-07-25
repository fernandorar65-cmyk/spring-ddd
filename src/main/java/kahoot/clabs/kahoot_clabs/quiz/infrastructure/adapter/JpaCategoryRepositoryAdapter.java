package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Category;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper.CategoryMapper;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.SpringDataCategoryRepository;

@Repository
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository springDataRepository;

    public JpaCategoryRepositoryAdapter(SpringDataCategoryRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Category save(Category category) {
        return CategoryMapper.toDomain(springDataRepository.save(CategoryMapper.toEntity(category)));
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
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
}
