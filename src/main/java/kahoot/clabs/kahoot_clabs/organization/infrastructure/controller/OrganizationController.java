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
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Organization signed up", signUpUseCase.execute(command)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> create(@Valid @RequestBody CreateOrganizationCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED, "Organization created", createOrganizationUseCase.execute(command)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Organization retrieved", getOrganizationUseCase.execute(new GetOrganizationQuery(id))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Organization updated", updateOrganizationUseCase.execute(id, command)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<OrganizationResponse>> addMember(
            @PathVariable UUID id,
            @Valid @RequestBody AddMemberCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Member added", addMemberUseCase.execute(id, command)));
    }

    @PostMapping("/{id}/invitations")
    public ResponseEntity<ApiResponse<OrganizationResponse>> inviteMember(
            @PathVariable UUID id,
            @Valid @RequestBody InviteMemberCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Member invited", inviteMemberUseCase.execute(id, command)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Member removed", removeMemberUseCase.execute(id, userId)));
    }
}
