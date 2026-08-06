package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "game_session_read_models")
@CompoundIndex(name = "idx_org_status", def = "{'organizationId': 1, 'status': 1}")
@Getter
@Setter
@NoArgsConstructor
public class GameSessionReadDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID organizationId;

    private UUID quizId;
    private UUID hostUserId;
    private String status;
    private int currentQuestionIndex;
    private int playerCount;
    private int questionCount;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
