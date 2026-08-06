package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;

public record GameSessionResponse(
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
        LocalDateTime updatedAt
) {

    public static GameSessionResponse from(GameSession session) {
        return new GameSessionResponse(
                session.getId(),
                session.getOrganizationId(),
                session.getQuizId(),
                session.getHostUserId(),
                session.getStatus().name(),
                session.getCurrentQuestionIndex(),
                session.getPlayers().size(),
                session.getQuestions().size(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getCreatedAt(),
                session.getUpdatedAt());
    }

    public static GameSessionResponse from(GameSessionReadModel readModel) {
        return new GameSessionResponse(
                readModel.id(),
                readModel.organizationId(),
                readModel.quizId(),
                readModel.hostUserId(),
                readModel.status(),
                readModel.currentQuestionIndex(),
                readModel.playerCount(),
                readModel.questionCount(),
                readModel.startedAt(),
                readModel.finishedAt(),
                readModel.createdAt(),
                readModel.updatedAt());
    }
}
