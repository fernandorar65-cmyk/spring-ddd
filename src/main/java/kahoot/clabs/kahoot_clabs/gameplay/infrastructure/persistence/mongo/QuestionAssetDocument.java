package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "question_assets")
@Getter
@Setter
@NoArgsConstructor
public class QuestionAssetDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private UUID questionId;

    private String type;
    private String url;
    private String thumbnailUrl;
    private String altText;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
