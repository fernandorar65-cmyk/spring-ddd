package kahoot.clabs.kahoot_clabs.identity.infrastructure.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.identity.application.command.AssignRoleCommand;
import kahoot.clabs.kahoot_clabs.identity.application.command.ChangePasswordCommand;
import kahoot.clabs.kahoot_clabs.identity.application.command.UpdateProfileCommand;
import kahoot.clabs.kahoot_clabs.identity.application.dto.UserProfileResponse;
import kahoot.clabs.kahoot_clabs.identity.application.query.GetUserProfileQuery;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.AssignRoleUseCase;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.ChangePasswordUseCase;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.GetUserProfileUseCase;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.UpdateProfileUseCase;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final AssignRoleUseCase assignRoleUseCase;

    public UserController(
            GetUserProfileUseCase getUserProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            AssignRoleUseCase assignRoleUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(getUserProfileUseCase.execute(new GetUserProfileQuery(id)));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfileCommand command) {
        return ResponseEntity.ok(updateProfileUseCase.execute(id, command));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordCommand command) {
        changePasswordUseCase.execute(id, command);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserProfileResponse> assignRole(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRoleCommand command) {
        return ResponseEntity.ok(assignRoleUseCase.execute(id, command));
    }
}
