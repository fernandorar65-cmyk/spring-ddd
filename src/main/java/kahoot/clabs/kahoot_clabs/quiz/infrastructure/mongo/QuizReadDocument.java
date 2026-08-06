package kahoot.clabs.kahoot_clabs.quiz.infrastructure.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "quiz_read_models")
@Getter
@Setter
@NoArgsConstructor
public class QuizReadDocument {

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
    private List<UUID> categoryIds = new ArrayList<>();
    private int questionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
