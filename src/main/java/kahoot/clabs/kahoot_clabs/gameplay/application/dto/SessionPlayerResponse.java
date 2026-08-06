package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;

public record SessionPlayerResponse(
        UUID id,
        UUID userId,
        String nickname,
        int score,
        boolean connected,
        LocalDateTime joinedAt,
        LocalDateTime leftAt
) {

    public static SessionPlayerResponse from(SessionPlayer player) {
        return new SessionPlayerResponse(
                player.getId(),
                player.getUserId(),
                player.getNickname().value(),
                player.getScore(),
                player.isConnected(),
                player.getJoinedAt(),
                player.getLeftAt());
    }
}
