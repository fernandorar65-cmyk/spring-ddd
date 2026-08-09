package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.CategoryReadPort;
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
