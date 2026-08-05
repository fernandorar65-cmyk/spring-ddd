package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;

public record LeaderboardEntryResponse(
        int rank,
        UUID playerId,
        UUID userId,
        String nickname,
        int score,
        boolean connected
) {

    public static List<LeaderboardEntryResponse> from(GameSession session) {
        AtomicInteger rank = new AtomicInteger(1);
        return session.leaderboard().stream()
                .map(player -> toEntry(rank.getAndIncrement(), player))
                .toList();
    }

    private static LeaderboardEntryResponse toEntry(int rank, SessionPlayer player) {
        return new LeaderboardEntryResponse(
                rank,
                player.getId(),
                player.getUserId(),
                player.getNickname().value(),
                player.getScore(),
                player.isConnected());
    }
}
