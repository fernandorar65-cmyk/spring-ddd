package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.PlayerRead;

public record LeaderboardEntryResponse(
        int rank,
        UUID playerId,
        UUID userId,
        String nickname,
        int score,
        boolean connected
) {

    public static List<LeaderboardEntryResponse> from(GameSessionReadModel readModel) {
        AtomicInteger rank = new AtomicInteger(1);
        return readModel.players().stream()
                .sorted(Comparator.comparingInt(PlayerRead::score)
                        .reversed()
                        .thenComparing(PlayerRead::joinedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(player -> new LeaderboardEntryResponse(
                        rank.getAndIncrement(),
                        player.id(),
                        player.userId(),
                        player.nickname(),
                        player.score(),
                        player.connected()))
                .toList();
    }
}
