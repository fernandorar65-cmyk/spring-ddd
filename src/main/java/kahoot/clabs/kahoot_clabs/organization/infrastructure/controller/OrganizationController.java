package kahoot.clabs.kahoot_clabs.organization.infrastructure.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import kahoot.clabs.kahoot_clabs.organization.application.command.CreateOrganizationCommand;
import kahoot.clabs.kahoot_clabs.organization.application.command.InviteMemberCommand;
import kahoot.clabs.kahoot_clabs.organization.application.command.UpdateOrganizationCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.application.query.GetOrganizationQuery;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.CreateOrganizationUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.GetOrganizationUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.InviteMemberUseCase;
import kahoot.clabs.kahoot_clabs.organization.application.usecase.UpdateOrganizationUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations", description = "Tenants, onboarding, miembros e invitaciones")
public class OrganizationController {
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final UpdateOrganizationUseCase updateOrganizationUseCase;
    private final GetOrganizationUseCase getOrganizationUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;

    public OrganizationController(
            CreateOrganizationUseCase createOrganizationUseCase,
            UpdateOrganizationUseCase updateOrganizationUseCase,
            GetOrganizationUseCase getOrganizationUseCase,
            InviteMemberUseCase inviteMemberUseCase
        ) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.updateOrganizationUseCase = updateOrganizationUseCase;
        this.getOrganizationUseCase = getOrganizationUseCase;
        this.inviteMemberUseCase = inviteMemberUseCase;
    }

    // TODO: crear oganizacion nada más
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear organización",
            description = "Crea una organización (tenant) con nombre, slug y opcionalmente un logo.")
    public ResponseEntity<ApiResponse<OrganizationResponse>> create(
            @RequestParam String name,
            @RequestParam String slug,
            @RequestParam(required = false) String description,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws IOException {
        CreateOrganizationCommand command = new CreateOrganizationCommand(name, slug, description);
        byte[] content = logo == null || logo.isEmpty() ? null : logo.getBytes();
        String contentType = logo == null || logo.isEmpty() ? null : logo.getContentType();
        String filename = logo == null || logo.isEmpty() ? null : logo.getOriginalFilename();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED,
                        "Organization created",
                        createOrganizationUseCase.execute(command, content, contentType, filename)));
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



    // **** mejorar logica de esto ya que al llegar el correo deberia crearse todo el usuario su rol y unirlo a la organización ****
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






}
