package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "quiz_categories")
@CompoundIndex(name = "uq_quiz_category", def = "{'quizId': 1, 'categoryId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
public class QuizCategoryDocument {

    @Id
    private String id;

    @Indexed
    private UUID quizId;

    @Indexed
    private UUID categoryId;

    public static String composeId(UUID quizId, UUID categoryId) {
        return quizId + ":" + categoryId;
    }
}
