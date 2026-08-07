package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private List<PlayerEmbedded> players = new ArrayList<>();
    private List<QuestionEmbedded> questions = new ArrayList<>();
    private List<AnswerEmbedded> answers = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlayerEmbedded {
        private UUID id;
        private UUID userId;
        private String nickname;
        private int score;
        private boolean connected;
        private LocalDateTime joinedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class QuestionEmbedded {
        private UUID id;
        private int orderIndex;
        private int points;
        private int timeLimitSeconds;
        private String title;
        private String description;
        private String questionType;
        private LocalDateTime openedAt;
        private LocalDateTime closedAt;
        private List<OptionEmbedded> options = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OptionEmbedded {
        private UUID id;
        private String text;
        private int orderIndex;
        private boolean correct;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AnswerEmbedded {
        private UUID id;
        private UUID sessionQuestionId;
        private UUID sessionPlayerId;
        private UUID sessionAnswerOptionId;
        private boolean correct;
        private long responseTimeMs;
        private int awardedPoints;
        private LocalDateTime answeredAt;
    }
}
