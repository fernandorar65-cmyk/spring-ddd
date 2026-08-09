package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "session_questions")
@Getter
@Setter
@NoArgsConstructor
public class SessionQuestionDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID sessionId;

    private UUID sourceQuestionId;
    private int orderIndex;
    private int points;
    private int timeLimitSeconds;
    private String title;
    private String description;
    private String questionType;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
