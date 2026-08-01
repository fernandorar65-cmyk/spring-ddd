package kahoot.clabs.kahoot_clabs.identity.infrastructure.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Consulta y administración de perfiles de usuario")
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
    @Operation(
            summary = "Obtener perfil de usuario",
            description = "Devuelve los datos de perfil y estado del usuario identificado por su UUID.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getById(
            @Parameter(description = "Identificador del usuario") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "User retrieved",
                getUserProfileUseCase.execute(new GetUserProfileQuery(id))));
    }

    @PutMapping(path = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar perfil",
            description = "Actualiza datos de perfil (teléfono, bio, ubicación) y opcionalmente sube una imagen de perfil.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Parameter(description = "Identificador del usuario") @PathVariable UUID id,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String location,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) throws IOException {
        UpdateProfileCommand command = new UpdateProfileCommand(phoneNumber, birthDate, bio, location);
        byte[] content = avatar == null || avatar.isEmpty() ? null : avatar.getBytes();
        String contentType = avatar == null || avatar.isEmpty() ? null : avatar.getContentType();
        String filename = avatar == null || avatar.isEmpty() ? null : avatar.getOriginalFilename();

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "User profile updated",
                updateProfileUseCase.execute(id, command, content, contentType, filename)));
    }

    @PutMapping("/{id}/password")
    @Operation(
            summary = "Cambiar contraseña",
            description = "Cambia la contraseña del usuario validando la contraseña actual y aplicando BCrypt a la nueva.")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "Identificador del usuario") @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordCommand command) {
        changePasswordUseCase.execute(id, command);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    @Operation(
            summary = "Asignar rol",
            description = "Asigna un rol de identidad (por tipo o id) al usuario indicado.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> assignRole(
            @Parameter(description = "Identificador del usuario") @PathVariable UUID id,
            @Valid @RequestBody AssignRoleCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "User role assigned", assignRoleUseCase.execute(id, command)));
    }
}
