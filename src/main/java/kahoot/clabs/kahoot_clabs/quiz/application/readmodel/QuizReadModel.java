package kahoot.clabs.kahoot_clabs.quiz.application.readmodel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Read model for quiz queries (Mongo). Independent from the domain aggregate.
 */
public record QuizReadModel(
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
        boolean randomQuestions,
        boolean randomAnswers,
        boolean showCorrectAnswer,
        boolean showRanking,
        boolean allowRetry,
        boolean showTimer,
        boolean musicEnabled,
        List<UUID> categoryIds,
        int questionCount,
        List<QuestionRead> questions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public QuizReadModel {
        categoryIds = categoryIds == null ? List.of() : List.copyOf(categoryIds);
        questions = questions == null ? List.of() : List.copyOf(questions);
    }

    public record QuestionRead(
            UUID id,
            UUID quizId,
            String title,
            String description,
            String type,
            String difficulty,
            String explanation,
            int orderIndex,
            int timeLimitSeconds,
            int points,
            List<OptionRead> options,
            AssetRead asset,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        public QuestionRead {
            options = options == null ? List.of() : List.copyOf(options);
        }
    }

    public record OptionRead(
            UUID id,
            UUID questionId,
            String text,
            boolean correct,
            String explanation,
            int orderIndex,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }

    public record AssetRead(
            UUID id,
            UUID questionId,
            String type,
            String url,
            String thumbnailUrl,
            String altText,
            Integer durationSeconds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }
}
