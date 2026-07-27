package kahoot.clabs.kahoot_clabs.organization.infrastructure.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.organization.application.command.AddMemberCommand;
import kahoot.clabs.kahoot_clabs.organization.application.command.CreateOrganizationCommand;
import kahoot.clabs.kahoot_clabs.organization.application.command.InviteMemberCommand;
import kahoot.clabs.kahoot_clabs.organization.application.command.SignUpCommand;
import kahoot.clabs.kahoot_clabs.organization.application.command.UpdateOrganizationCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.application.dto.SignUpResponse;
import kahoot.clabs.kahoot_clabs.organization.application.query.GetOrganizationQuery;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.AddMemberUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.CreateOrganizationUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.GetOrganizationUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.InviteMemberUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.RemoveMemberUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.SignUpUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.UpdateOrganizationUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations", description = "Tenants, onboarding, miembros e invitaciones")
public class OrganizationController {

    private final SignUpUseCase signUpUseCase;
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final UpdateOrganizationUseCase updateOrganizationUseCase;
    private final GetOrganizationUseCase getOrganizationUseCase;
    private final AddMemberUseCase addMemberUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;

    public OrganizationController(
            SignUpUseCase signUpUseCase,
            CreateOrganizationUseCase createOrganizationUseCase,
            UpdateOrganizationUseCase updateOrganizationUseCase,
            GetOrganizationUseCase getOrganizationUseCase,
            AddMemberUseCase addMemberUseCase,
            InviteMemberUseCase inviteMemberUseCase,
            RemoveMemberUseCase removeMemberUseCase) {
        this.signUpUseCase = signUpUseCase;
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.updateOrganizationUseCase = updateOrganizationUseCase;
        this.getOrganizationUseCase = getOrganizationUseCase;
        this.addMemberUseCase = addMemberUseCase;
        this.inviteMemberUseCase = inviteMemberUseCase;
        this.removeMemberUseCase = removeMemberUseCase;
    }

    @PostMapping("/signup")
    @Operation(
            summary = "Signup de organización",
            description = "Onboarding: crea el usuario administrador, le asigna rol y crea la organización en un solo flujo.")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Organization signed up", signUpUseCase.execute(command)));
    }

    @PostMapping
    @Operation(
            summary = "Crear organización",
            description = "Crea una organización (tenant) con nombre y slug únicos, asociada a un usuario creador.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> create(@Valid @RequestBody CreateOrganizationCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED, "Organization created", createOrganizationUseCase.execute(command)));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener organización",
            description = "Devuelve los datos de la organización y su listado de miembros.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getById(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Organization retrieved", getOrganizationUseCase.execute(new GetOrganizationQuery(id))));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar organización",
            description = "Actualiza nombre, descripción u otros datos editables de la organización.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> update(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Organization updated", updateOrganizationUseCase.execute(id, command)));
    }

    @PostMapping("/{id}/members")
    @Operation(
            summary = "Agregar miembro",
            description = "Agrega un usuario existente como miembro activo de la organización con un rol interno.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> addMember(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID id,
            @Valid @RequestBody AddMemberCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Member added", addMemberUseCase.execute(id, command)));
    }

    @PostMapping("/{id}/invitations")
    @Operation(
            summary = "Invitar miembro",
            description = "Invita a un usuario (por email) a unirse a la organización con un rol propuesto.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> inviteMember(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID id,
            @Valid @RequestBody InviteMemberCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Member invited", inviteMemberUseCase.execute(id, command)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(
            summary = "Remover miembro",
            description = "Elimina o da de baja a un miembro de la organización.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> removeMember(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID id,
            @Parameter(description = "Identificador del usuario miembro") @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Member removed", removeMemberUseCase.execute(id, userId)));
    }
}
