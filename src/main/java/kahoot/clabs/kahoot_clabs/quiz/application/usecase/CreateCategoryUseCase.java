package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.command.CreateCategoryCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Category;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;

@Service
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CreateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse execute(CreateCategoryCommand command) {
        Category category = Category.create(command.organizationId(), command.name());
        category.changeDescription(command.description());
        category.changeColor(command.color());
        category.changeIcon(command.icon());
        return CategoryResponse.from(categoryRepository.save(category));
    }
}
