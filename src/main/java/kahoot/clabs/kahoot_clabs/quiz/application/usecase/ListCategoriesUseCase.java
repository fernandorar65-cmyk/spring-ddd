package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.port.CategoryReadPort;

@Service
public class ListCategoriesUseCase {

    private final CategoryReadPort categoryReadPort;

    public ListCategoriesUseCase(CategoryReadPort categoryReadPort) {
        this.categoryReadPort = categoryReadPort;
    }

    public List<CategoryResponse> execute(UUID organizationId) {
        return categoryReadPort.findByOrganizationId(organizationId).stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
