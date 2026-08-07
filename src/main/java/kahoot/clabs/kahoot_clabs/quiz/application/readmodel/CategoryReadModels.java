package kahoot.clabs.kahoot_clabs.quiz.application.readmodel;

import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Category;

public final class CategoryReadModels {

    private CategoryReadModels() {
    }

    public static CategoryReadModel from(Category category) {
        return new CategoryReadModel(
                category.getId(),
                category.getOrganizationId(),
                category.getName(),
                category.getDescription(),
                category.getColor(),
                category.getIcon());
    }
}
