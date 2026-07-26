package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.PlayerRank;

public record LeaderboardResponse(UUID sessionId, List<PlayerResponse> players) {
    public static LeaderboardResponse from(GameSession session) {
        return new LeaderboardResponse(
                session.getId(),
                session.leaderboard().stream().map(rank -> PlayerResponse.from(session, rank)).toList());
    }

    public record PlayerResponse(
            int position,
            UUID sessionPlayerId,
            String nickname,
            int score,
            int correctAnswers,
            long totalResponseTimeMs
    ) {
        private static PlayerResponse from(GameSession session, PlayerRank rank) {
            List<PlayerAnswer> answers = session.getQuestions().stream()
                    .flatMap(question -> question.getAnswers().stream())
                    .filter(answer -> answer.getSessionPlayerId().equals(rank.playerId()))
                    .toList();
            return new PlayerResponse(
                    rank.position(),
                    rank.playerId(),
                    rank.nickname(),
                    rank.score().value(),
                    (int) answers.stream().filter(PlayerAnswer::isCorrect).count(),
                    answers.stream().mapToLong(answer -> answer.getResponseTime().toMillis()).sum());
        }
    }
}
