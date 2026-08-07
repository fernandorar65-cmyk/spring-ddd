package kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "categories")
@CompoundIndex(
        name = "uq_categories_organization_name",
        def = "{'organizationId': 1, 'name': 1}",
        unique = true)
@Getter
@Setter
@NoArgsConstructor
public class CategoryDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID organizationId;

    private String name;
    private String description;
    private String color;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
