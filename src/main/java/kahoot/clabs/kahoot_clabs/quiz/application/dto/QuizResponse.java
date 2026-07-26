package kahoot.clabs.kahoot_clabs.quiz.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;

public record QuizResponse(
        UUID id,
        UUID organizationId,
        UUID createdById,
        String title,
        String description,
        String thumbnail,
        String visibility,
        String status,
        String difficulty,
        Long estimatedTimeMinutes,
        int playCount,
        double averageRating,
        boolean template,
        List<UUID> categoryIds,
        int questionCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static QuizResponse from(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getOrganizationId(),
                quiz.getCreatedById(),
                quiz.getTitle().value(),
                quiz.getDescription(),
                quiz.getThumbnail(),
                quiz.getVisibility().name(),
                quiz.getStatus().name(),
                quiz.getDifficulty().name(),
                quiz.getEstimatedTime() == null ? null : quiz.getEstimatedTime().toMinutes(),
                quiz.getPlayCount(),
                quiz.getAverageRating(),
                quiz.isTemplate(),
                quiz.getCategories().stream().map(category -> category.getCategoryId()).toList(),
                quiz.getQuestions().size(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }
}
