package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.port.CategoryReadPort;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetCategoryUseCase {

    private final CategoryReadPort categoryReadPort;

    public GetCategoryUseCase(CategoryReadPort categoryReadPort) {
        this.categoryReadPort = categoryReadPort;
    }

    public CategoryResponse execute(UUID id) {
        return categoryReadPort.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(() -> new DomainException("Category not found: " + id));
    }
}
