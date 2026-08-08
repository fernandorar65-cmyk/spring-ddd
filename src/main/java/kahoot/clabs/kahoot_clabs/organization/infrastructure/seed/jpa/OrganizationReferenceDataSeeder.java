package kahoot.clabs.kahoot_clabs.organization.infrastructure.seed.jpa;

import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.MemberStatus;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationStatus;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationMemberStatusCatalogEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationStatusCatalogEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationMemberStatusCatalogJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationStatusCatalogJpaRepository;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;

@Component
public class OrganizationReferenceDataSeeder implements DataSeeder {

    private final OrganizationStatusCatalogJpaRepository organizationStatusRepository;
    private final OrganizationMemberStatusCatalogJpaRepository memberStatusRepository;
    // private final ObjectProvider<OrganizationCatalogProjectionPort> catalogProjectionPort;

    public OrganizationReferenceDataSeeder(
            OrganizationStatusCatalogJpaRepository organizationStatusRepository,
            OrganizationMemberStatusCatalogJpaRepository memberStatusRepository
            // ObjectProvider<OrganizationCatalogProjectionPort> catalogProjectionPort
        ) {
        this.organizationStatusRepository = organizationStatusRepository;
        this.memberStatusRepository = memberStatusRepository;
        // this.catalogProjectionPort = catalogProjectionPort;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public String name() {
        return "organization-reference-data";
    }

    @Override
    public void seed() {
        seedOrganizationStatuses();
        seedMemberStatuses();
    }

    private void seedOrganizationStatuses() {
        for (OrganizationStatus status : OrganizationStatus.values()) {
            OrganizationStatusCatalogEntity entity = new OrganizationStatusCatalogEntity();
            entity.setId(UUID.randomUUID());
            entity.setName(status.name());
            entity.setDescription("description base");
            organizationStatusRepository.save(entity);
            // catalogProjectionPort.ifAvailable(port -> port.saveOrganizationStatus(
                    // entity.getId(), entity.getName(), entity.getDescription()));
        }
    }

    private void seedMemberStatuses() {
        for (MemberStatus status : MemberStatus.values()) {
            OrganizationMemberStatusCatalogEntity entity = new OrganizationMemberStatusCatalogEntity();
            entity.setId(UUID.randomUUID());
            entity.setName(status.name());
            entity.setDescription("description base");
            memberStatusRepository.save(entity);
            // catalogProjectionPort.ifAvailable(port -> port.saveMemberStatus(
                    // entity.getId(), entity.getName(), entity.getDescription()));
        }
    }
}
