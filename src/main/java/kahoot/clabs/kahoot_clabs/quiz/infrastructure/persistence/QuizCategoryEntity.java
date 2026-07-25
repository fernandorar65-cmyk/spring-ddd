package kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_categories")
@IdClass(QuizCategoryEntity.PrimaryKey.class)
@Getter
@Setter
@NoArgsConstructor
public class QuizCategoryEntity {

    @Id
    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @Id
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    public static final class PrimaryKey implements Serializable {
        private UUID quizId;
        private UUID categoryId;

        public PrimaryKey() {
        }

        public PrimaryKey(UUID quizId, UUID categoryId) {
            this.quizId = quizId;
            this.categoryId = categoryId;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PrimaryKey that)) {
                return false;
            }
            return Objects.equals(quizId, that.quizId) && Objects.equals(categoryId, that.categoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(quizId, categoryId);
        }
    }
}
