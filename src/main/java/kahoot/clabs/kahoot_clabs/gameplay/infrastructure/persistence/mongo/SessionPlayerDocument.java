package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "session_players")
@Getter
@Setter
@NoArgsConstructor
public class SessionPlayerDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID sessionId;

    @Indexed
    private UUID userId;

    private String nickname;
    private int score;
    private boolean connected;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
