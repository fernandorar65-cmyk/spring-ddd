package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.RoleNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.organization.application.command.InviteMemberCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

@Service
public class InviteMemberUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public InviteMemberUseCase(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public OrganizationResponse execute(UUID organizationId, InviteMemberCommand command) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UserNotFoundException(command.email()));
        Role role = roleRepository.findByType(command.roleType())
                .orElseThrow(() -> new RoleNotFoundException(command.roleType()));

        organization.inviteMember(user.getId(), role.getId());
        return OrganizationResponse.from(organizationRepository.save(organization));
    }
}
