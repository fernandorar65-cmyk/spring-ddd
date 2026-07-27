package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;

public record GameSessionResponse(
        UUID id,
        UUID organizationId,
        UUID quizId,
        UUID hostUserId,
        String pin,
        String status,
        int currentQuestionIndex,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PlayerResponse> players,
        List<QuestionResponse> questions
) {

    public static GameSessionResponse from(GameSession session) {
        return new GameSessionResponse(
                session.getId(),
                session.getOrganizationId(),
                session.getQuizId(),
                session.getHostUserId(),
                session.getPin() == null ? null : session.getPin().value(),
                session.getStatus().name(),
                session.getCurrentQuestionIndex(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                session.getPlayers().stream().map(PlayerResponse::from).toList(),
                session.getQuestions().stream().map(QuestionResponse::from).toList());
    }

    public record PlayerResponse(
            UUID id,
            UUID userId,
            String nickname,
            int score,
            boolean connected,
            LocalDateTime joinedAt,
            LocalDateTime leftAt
    ) {

        private static PlayerResponse from(SessionPlayer player) {
            return new PlayerResponse(
                    player.getId(),
                    player.getUserId(),
                    player.getNickname(),
                    player.getScore().value(),
                    player.isConnected(),
                    player.getJoinedAt(),
                    player.getLeftAt());
        }
    }

    public record QuestionResponse(
            UUID id,
            UUID quizQuestionId,
            int orderIndex,
            int points,
            int timeLimitSeconds,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            int answerCount
    ) {

        private static QuestionResponse from(SessionQuestion question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getQuizQuestionId(),
                    question.getOrderIndex(),
                    question.getPoints(),
                    question.getTimeLimitSeconds(),
                    question.getOpenedAt(),
                    question.getClosedAt(),
                    question.getAnswers().size());
        }
    }
}
