package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "session_questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_session_questions_session_order",
                columnNames = {"session_id", "order_index"}))
@Getter
@Setter
@NoArgsConstructor
public class SessionQuestionEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "quiz_question_id", nullable = false)
    private UUID quizQuestionId;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private int points;

    @Column(name = "time_limit_seconds", nullable = false)
    private int timeLimitSeconds;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
