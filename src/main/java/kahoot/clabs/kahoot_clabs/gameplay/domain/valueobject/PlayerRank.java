package kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Position of a player in the leaderboard of a session.
 */
public record PlayerRank(int position, UUID playerId, String nickname, PlayerScore score) {

    public PlayerRank {
        if (position < 1) {
            throw new DomainException("Rank position must be at least 1");
        }
        if (playerId == null) {
            throw new DomainException("Player id is required");
        }
        if (score == null) {
            throw new DomainException("Player score is required");
        }
    }
}
