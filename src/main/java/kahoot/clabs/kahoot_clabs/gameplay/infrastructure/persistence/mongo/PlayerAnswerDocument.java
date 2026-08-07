package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "player_answers")
@Getter
@Setter
@NoArgsConstructor
public class PlayerAnswerDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID sessionQuestionId;

    @Indexed
    private UUID sessionPlayerId;

    private UUID sessionAnswerOptionId;
    private boolean correct;
    private long responseTimeMs;
    private int awardedPoints;
    private LocalDateTime answeredAt;
}
