package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.command.AssignRoleCommand;
import kahoot.clabs.kahoot_clabs.identity.application.command.RegisterUserCommand;
import kahoot.clabs.kahoot_clabs.identity.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.AssignRoleUseCase;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.RegisterUserUseCase;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.RoleNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.organization.application.command.SignUpCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.SignUpResponse;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationSlugAlreadyTakenException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationSlug;

/**
 * Onboarding flow: creates the tenant and registers its first administrator.
 * Identity owns the user, organization owns the membership.
 */
@Service
public class SignUpUseCase {

    private final OrganizationRepository organizationRepository;
    private final RegisterUserUseCase registerUserUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final RoleRepository roleRepository;

    public SignUpUseCase(
            OrganizationRepository organizationRepository,
            RegisterUserUseCase registerUserUseCase,
            AssignRoleUseCase assignRoleUseCase,
            RoleRepository roleRepository) {
        this.organizationRepository = organizationRepository;
        this.registerUserUseCase = registerUserUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public SignUpResponse execute(SignUpCommand command) {
        String slug = OrganizationSlug.of(command.organizationSlug()).value();
        if (organizationRepository.existsBySlug(slug)) {
            throw new OrganizationSlugAlreadyTakenException(slug);
        }

        Role ownerRole = roleRepository.findByType(RoleType.OWNER_ORGANIZATION)
                .orElseThrow(() -> new RoleNotFoundException(RoleType.OWNER_ORGANIZATION));

        AuthUserResponse owner = registerUserUseCase.execute(new RegisterUserCommand(
                command.email(),
                command.firstName(),
                command.lastName(),
                command.password()));
        assignRoleUseCase.execute(owner.userId(), new AssignRoleCommand(RoleType.OWNER_ORGANIZATION));

        Organization organization = Organization.create(command.organizationName(), slug);
        organization.addMember(owner.userId(), ownerRole.getId());
        organization = organizationRepository.save(organization);

        return new SignUpResponse(
                organization.getId(),
                organization.getSlug().value(),
                owner.userId(),
                owner.email(),
                owner.firstName(),
                owner.lastName());
    }
}
