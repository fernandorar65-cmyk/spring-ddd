package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.RoleNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.organization.application.command.AddMemberCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

@Service
public class AddMemberUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AddMemberUseCase(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public OrganizationResponse execute(UUID organizationId, AddMemberCommand command) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        if (userRepository.findById(command.userId()).isEmpty()) {
            throw new UserNotFoundException(command.userId());
        }
        Role role = roleRepository.findByType(command.roleType())
                .orElseThrow(() -> new RoleNotFoundException(command.roleType()));

        organization.addMember(command.userId(), role.getId());
        return OrganizationResponse.from(organizationRepository.save(organization));
    }
}
