package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public GetCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public CategoryResponse execute(UUID id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(() -> new DomainException("Category not found: " + id));
    }
}
