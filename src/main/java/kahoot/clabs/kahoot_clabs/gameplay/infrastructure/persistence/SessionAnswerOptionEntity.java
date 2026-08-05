package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence;

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
@Table(name = "session_answer_options")
@Getter
@Setter
@NoArgsConstructor
public class SessionAnswerOptionEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "session_question_id", nullable = false)
    private UUID sessionQuestionId;

    @Column(name = "source_answer_option_id")
    private UUID sourceAnswerOptionId;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_question_id", insertable = false, updatable = false)
    private SessionQuestionEntity sessionQuestion;
}
