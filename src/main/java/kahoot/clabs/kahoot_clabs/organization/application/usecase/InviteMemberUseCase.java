package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.organization.application.command.InviteMemberCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.application.port.integration.UserDirectoryPort;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class InviteMemberUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserDirectoryPort userDirectoryPort;

    public InviteMemberUseCase(
            OrganizationRepository organizationRepository,
            UserDirectoryPort userDirectoryPort) {
        this.organizationRepository = organizationRepository;
        this.userDirectoryPort = userDirectoryPort;
    }

    @Transactional
    public OrganizationResponse execute(UUID organizationId, InviteMemberCommand command) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        UUID userId = userDirectoryPort.findUserIdByEmail(command.email())
                .orElseThrow(() -> new DomainException("User not found: " + command.email()));
        UUID roleId = userDirectoryPort.findRoleIdByType(command.roleType())
                .orElseThrow(() -> new DomainException("Role not found: " + command.roleType()));

        organization.inviteMember(userId, roleId);
        return OrganizationResponse.from(organizationRepository.save(organization));
    }
}
