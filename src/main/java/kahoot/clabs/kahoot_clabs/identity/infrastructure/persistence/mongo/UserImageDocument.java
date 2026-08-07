package kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "user_images")
@Getter
@Setter
@NoArgsConstructor
public class UserImageDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID userId;

    private String url;
    private String type;
    private String alt;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
