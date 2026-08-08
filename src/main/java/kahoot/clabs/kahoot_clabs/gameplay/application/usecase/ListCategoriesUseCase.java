package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.CategoryReadPort;

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
