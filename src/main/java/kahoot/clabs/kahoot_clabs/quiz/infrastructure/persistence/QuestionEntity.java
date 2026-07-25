package kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence;

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
        name = "questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_questions_quiz_order",
                columnNames = {"quiz_id", "order_index"}))
@Getter
@Setter
@NoArgsConstructor
public class QuestionEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "time_limit_seconds", nullable = false)
    private int timeLimitSeconds;

    @Column(nullable = false)
    private int points;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
