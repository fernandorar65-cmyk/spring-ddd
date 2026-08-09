package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.QuizReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.AnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.QuestionAsset;

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

    /** Full details projection from the Mongo read model (includes questions). */
    public static QuizResponse fromDetails(QuizReadModel readModel) {
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
                readModel.questions().stream().map(QuestionResponse::from).toList(),
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

        private static QuestionResponse from(QuizReadModel.QuestionRead question) {
            return new QuestionResponse(
                    question.id(),
                    question.title(),
                    question.description(),
                    question.type(),
                    question.difficulty(),
                    question.points(),
                    question.timeLimitSeconds(),
                    question.orderIndex(),
                    question.options().stream().map(AnswerOptionResponse::from).toList(),
                    QuestionAssetResponse.from(question.asset()));
        }
    }

    public record AnswerOptionResponse(UUID id, String text, int orderIndex) {

        private static AnswerOptionResponse from(AnswerOption option) {
            return new AnswerOptionResponse(option.getId(), option.getText(), option.getOrderIndex());
        }

        private static AnswerOptionResponse from(QuizReadModel.OptionRead option) {
            return new AnswerOptionResponse(option.id(), option.text(), option.orderIndex());
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

        private static QuestionAssetResponse from(QuizReadModel.AssetRead asset) {
            if (asset == null) {
                return null;
            }
            return new QuestionAssetResponse(
                    asset.id(),
                    asset.type(),
                    asset.url(),
                    asset.thumbnailUrl(),
                    asset.altText(),
                    asset.durationSeconds());
        }
    }
}
