package kahoot.clabs.kahoot_clabs.users.infrastructure.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kahoot.clabs.kahoot_clabs.users.application.dto.UserProfileResponse;
import kahoot.clabs.kahoot_clabs.users.application.usecase.GetUserProfileUseCase;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;

    public UserController(GetUserProfileUseCase getUserProfileUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> list() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("message", "users ping ok"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(getUserProfileUseCase.execute(id));
    }
}
