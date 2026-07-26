package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (!categoryRepository.findById(id).isPresent()) {
            throw new DomainException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
