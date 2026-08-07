package kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "quizzes")
@Getter
@Setter
@NoArgsConstructor
public class QuizDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID organizationId;

    private UUID createdById;
    private String title;
    private String description;
    private String thumbnail;
    private String status;
    private String difficulty;
    private Long estimatedTimeMinutes;
    private int playCount;
    private double averageRating;
    private boolean template;
    private boolean randomQuestions;
    private boolean randomAnswers;
    private boolean showCorrectAnswer;
    private boolean showRanking;
    private boolean allowRetry;
    private boolean showTimer;
    private boolean musicEnabled;
    /** Denormalized for list queries. */
    private int questionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
