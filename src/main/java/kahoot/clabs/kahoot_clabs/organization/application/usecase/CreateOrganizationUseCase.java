package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.organization.application.command.CreateOrganizationCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationSlugAlreadyTakenException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationSlug;

@Service
public class CreateOrganizationUseCase {

    private final OrganizationRepository organizationRepository;

    public CreateOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationResponse execute(CreateOrganizationCommand command) {
        String slug = OrganizationSlug.of(command.slug()).value();
        if (organizationRepository.existsBySlug(slug)) {
            throw new OrganizationSlugAlreadyTakenException(slug);
        }

        Organization organization = Organization.create(command.name(), slug);
        return OrganizationResponse.from(organizationRepository.save(organization));
    }
}
