package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.command.UpdateCategoryCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Category;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public UpdateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse execute(UUID id, UpdateCategoryCommand command) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DomainException("Category not found: " + id));
        category.rename(command.name());
        category.changeDescription(command.description());
        category.changeColor(command.color());
        category.changeIcon(command.icon());
        return CategoryResponse.from(categoryRepository.save(category));
    }
}
