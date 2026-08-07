package kahoot.clabs.kahoot_clabs.organization.infrastructure.seed;

import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.MemberStatus;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationStatus;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationMemberStatusCatalogEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationStatusCatalogEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationMemberStatusCatalogJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationStatusCatalogJpaRepository;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;

@Component
public class OrganizationReferenceDataSeeder implements DataSeeder {

    private final OrganizationStatusCatalogJpaRepository organizationStatusRepository;
    private final OrganizationMemberStatusCatalogJpaRepository memberStatusRepository;
    private final ObjectProvider<OrganizationCatalogProjectionPort> catalogProjectionPort;

    public OrganizationReferenceDataSeeder(
            OrganizationStatusCatalogJpaRepository organizationStatusRepository,
            OrganizationMemberStatusCatalogJpaRepository memberStatusRepository,
            ObjectProvider<OrganizationCatalogProjectionPort> catalogProjectionPort) {
        this.organizationStatusRepository = organizationStatusRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.catalogProjectionPort = catalogProjectionPort;
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
        for (OrganizationStatus status : OrganizationStatus.values()) {
            ensureOrganizationStatus(status.name(), status.getDescription());
        }
        for (MemberStatus status : MemberStatus.values()) {
            ensureMemberStatus(status.name(), status.getDescription());
        }
    }

    private void ensureOrganizationStatus(String name, String description) {
        if (organizationStatusRepository.existsByName(name)) {
            return;
        }
        OrganizationStatusCatalogEntity entity = new OrganizationStatusCatalogEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription(truncate(description, 100));
        organizationStatusRepository.save(entity);
        catalogProjectionPort.ifAvailable(port -> port.saveOrganizationStatus(
                entity.getId(), entity.getName(), entity.getDescription()));
    }

    private void ensureMemberStatus(String name, String description) {
        if (memberStatusRepository.existsByName(name)) {
            return;
        }
        OrganizationMemberStatusCatalogEntity entity = new OrganizationMemberStatusCatalogEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription(truncate(description, 100));
        memberStatusRepository.save(entity);
        catalogProjectionPort.ifAvailable(port -> port.saveMemberStatus(
                entity.getId(), entity.getName(), entity.getDescription()));
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
