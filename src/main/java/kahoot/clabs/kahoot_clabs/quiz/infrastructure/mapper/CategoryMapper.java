package kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper;

import java.time.LocalDateTime;

import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Category;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.CategoryEntity;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryEntity toEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.getId());
        entity.setOrganizationId(category.getOrganizationId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setColor(category.getColor());
        entity.setIcon(category.getIcon());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    public static Category toDomain(CategoryEntity entity) {
        return Category.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getDescription(),
                entity.getColor(),
                entity.getIcon());
    }
}
