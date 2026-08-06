package kahoot.clabs.kahoot_clabs.gameplay.application.readmodel;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;

public final class GameSessionReadModels {

    private GameSessionReadModels() {
    }

    public static GameSessionReadModel from(GameSession session) {
        return new GameSessionReadModel(
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
}
