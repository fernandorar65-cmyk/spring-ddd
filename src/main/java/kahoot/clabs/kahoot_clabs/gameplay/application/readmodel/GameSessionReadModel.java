package kahoot.clabs.kahoot_clabs.gameplay.application.readmodel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Read model for game session queries (Mongo). Independent from the domain aggregate.
 */
public record GameSessionReadModel(
        UUID id,
        UUID organizationId,
        UUID quizId,
        UUID hostUserId,
        String status,
        int currentQuestionIndex,
        int playerCount,
        int questionCount,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PlayerRead> players,
        List<QuestionRead> questions,
        List<AnswerRead> answers) {

    public GameSessionReadModel {
        players = players == null ? List.of() : List.copyOf(players);
        questions = questions == null ? List.of() : List.copyOf(questions);
        answers = answers == null ? List.of() : List.copyOf(answers);
    }

    public record PlayerRead(
            UUID id,
            UUID userId,
            String nickname,
            int score,
            boolean connected,
            LocalDateTime joinedAt) {
    }

    public record QuestionRead(
            UUID id,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            String title,
            String description,
            String questionType,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            List<OptionRead> options) {

        public QuestionRead {
            options = options == null ? List.of() : List.copyOf(options);
        }
    }

    public record OptionRead(UUID id, String text, int orderIndex, boolean correct) {
    }

    public record AnswerRead(
            UUID id,
            UUID sessionQuestionId,
            UUID sessionPlayerId,
            UUID sessionAnswerOptionId,
            boolean correct,
            long responseTimeMs,
            int awardedPoints,
            LocalDateTime answeredAt) {
    }
}
