package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;

@Service
public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public ListCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> execute(UUID organizationId) {
        return categoryRepository.findByOrganizationId(organizationId).stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
