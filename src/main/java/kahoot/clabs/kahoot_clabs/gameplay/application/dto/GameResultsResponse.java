package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GameStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public record GameResultsResponse(
        UUID sessionId,
        String status,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        List<PlayerResultResponse> ranking
) {
    public static GameResultsResponse from(GameSession session) {
        if (session.getStatus() != GameStatus.FINISHED && session.getStatus() != GameStatus.CANCELLED) {
            throw new DomainException("Results are only available for finished or cancelled sessions");
        }
        return new GameResultsResponse(
                session.getId(),
                session.getStatus().name(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.leaderboard().stream().map(rank -> {
                    List<PlayerAnswer> answers = session.getQuestions().stream()
                            .flatMap(question -> question.getAnswers().stream())
                            .filter(answer -> answer.getSessionPlayerId().equals(rank.playerId()))
                            .toList();
                    int correct = (int) answers.stream().filter(PlayerAnswer::isCorrect).count();
                    return new PlayerResultResponse(
                            rank.position(), rank.playerId(), rank.nickname(), rank.score().value(),
                            correct, answers.size() - correct);
                }).toList());
    }

    public record PlayerResultResponse(
            int position,
            UUID sessionPlayerId,
            String nickname,
            int score,
            int correctAnswers,
            int incorrectAnswers
    ) {
    }
}
