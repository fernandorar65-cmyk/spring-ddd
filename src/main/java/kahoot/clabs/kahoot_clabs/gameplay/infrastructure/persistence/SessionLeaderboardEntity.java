package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "session_leaderboard",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_session_leaderboard_position",
                        columnNames = {"session_id", "position"}),
                @UniqueConstraint(
                        name = "uq_session_leaderboard_player",
                        columnNames = {"session_id", "session_player_id"})
        })
@Getter
@Setter
@NoArgsConstructor
public class SessionLeaderboardEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "session_player_id", nullable = false)
    private UUID sessionPlayerId;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false)
    private int score;

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private GameSessionEntity session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_player_id", insertable = false, updatable = false)
    private SessionPlayerEntity player;
}
