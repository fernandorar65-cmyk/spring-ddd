package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "question_type", length = 20)
    private String questionType;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private GameSessionEntity session;

    @OneToMany(mappedBy = "sessionQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlayerAnswerEntity> answers = new ArrayList<>();

    @OneToMany(mappedBy = "sessionQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SessionAnswerOptionEntity> options = new ArrayList<>();
}
