package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "session_answer_options")
@Getter
@Setter
@NoArgsConstructor
public class SessionAnswerOptionDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID sessionQuestionId;

    private UUID sourceAnswerOptionId;
    private String text;
    private boolean correct;
    private int orderIndex;
}
