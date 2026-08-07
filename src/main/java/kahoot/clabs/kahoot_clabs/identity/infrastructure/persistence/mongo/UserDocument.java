package kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID roleId;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String status;
    private String phoneNumber;
    private LocalDate birthDate;
    private String bio;
    private String location;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
