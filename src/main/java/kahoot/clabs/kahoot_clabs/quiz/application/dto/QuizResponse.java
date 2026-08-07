package kahoot.clabs.kahoot_clabs.quiz.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.QuestionAsset;

public record QuizResponse(
        UUID id,
        UUID organizationId,
        UUID createdById,
        String title,
        String description,
        String thumbnail,
        String status,
        String difficulty,
        Long estimatedTimeMinutes,
        int playCount,
        double averageRating,
        boolean template,
        List<UUID> categoryIds,
        int questionCount,
        List<QuestionResponse> questions,
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
                quiz.getStatus().name(),
                quiz.getDifficulty().name(),
                quiz.getEstimatedTime() == null ? null : quiz.getEstimatedTime().toMinutes(),
                quiz.getPlayCount(),
                quiz.getAverageRating(),
                quiz.isTemplate(),
                quiz.getCategories().stream().map(category -> category.getCategoryId()).toList(),
                quiz.getQuestions().size(),
                quiz.getQuestions().stream().map(QuestionResponse::from).toList(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }

    /** Summary projection from the Mongo read model (no question payloads). */
    public static QuizResponse from(QuizReadModel readModel) {
        return new QuizResponse(
                readModel.id(),
                readModel.organizationId(),
                readModel.createdById(),
                readModel.title(),
                readModel.description(),
                readModel.thumbnail(),
                readModel.status(),
                readModel.difficulty(),
                readModel.estimatedTimeMinutes(),
                readModel.playCount(),
                readModel.averageRating(),
                readModel.template(),
                readModel.categoryIds() == null ? List.of() : readModel.categoryIds(),
                readModel.questionCount(),
                List.of(),
                readModel.createdAt(),
                readModel.updatedAt());
    }

    public record QuestionResponse(
            UUID id,
            String title,
            String description,
            String type,
            String difficulty,
            int points,
            int timeLimitSeconds,
            int orderIndex,
            List<AnswerOptionResponse> options,
            QuestionAssetResponse asset) {

        private static QuestionResponse from(Question question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getType().name(),
                    question.getDifficulty().name(),
                    question.getPoints().value(),
                    question.getTimeLimit().seconds(),
                    question.getOrderIndex(),
                    question.getOptions().stream().map(AnswerOptionResponse::from).toList(),
                    QuestionAssetResponse.from(question.getAsset()));
        }
    }

    public record AnswerOptionResponse(UUID id, String text, int orderIndex) {

        private static AnswerOptionResponse from(AnswerOption option) {
            return new AnswerOptionResponse(option.getId(), option.getText(), option.getOrderIndex());
        }
    }

    public record QuestionAssetResponse(
            UUID id,
            String type,
            String url,
            String thumbnailUrl,
            String altText,
            Integer durationSeconds) {

        private static QuestionAssetResponse from(QuestionAsset asset) {
            if (asset == null) {
                return null;
            }
            return new QuestionAssetResponse(
                    asset.getId(),
                    asset.getType().name(),
                    asset.getUrl().value(),
                    asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value(),
                    asset.getAltText(),
                    asset.getDurationSeconds());
        }
    }
}
