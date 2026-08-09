package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "questions")
@Getter
@Setter
@NoArgsConstructor
public class QuestionDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID quizId;

    private String title;
    private String description;
    private String type;
    private String difficulty;
    private String explanation;
    private int orderIndex;
    private int timeLimitSeconds;
    private int points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
