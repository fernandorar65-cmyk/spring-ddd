package kahoot.clabs.kahoot_clabs.organization.infrastructure.seed.jpa;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.organization.application.event.MemberStatusCatalogUpsertedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.event.OrganizationStatusCatalogUpsertedEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    public OrganizationReferenceDataSeeder(
            OrganizationStatusCatalogJpaRepository organizationStatusRepository,
            OrganizationMemberStatusCatalogJpaRepository memberStatusRepository,
            ApplicationEventPublisher eventPublisher) {
        this.organizationStatusRepository = organizationStatusRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.eventPublisher = eventPublisher;
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
            entity.setDescription(status.getDescription() != null ? status.getDescription() : "description base");
            organizationStatusRepository.save(entity);
            eventPublisher.publishEvent(new OrganizationStatusCatalogUpsertedEvent(
                    entity.getId(), entity.getName(), entity.getDescription()));
        }
    }

    private void seedMemberStatuses() {
        for (MemberStatus status : MemberStatus.values()) {
            OrganizationMemberStatusCatalogEntity entity = new OrganizationMemberStatusCatalogEntity();
            entity.setId(UUID.randomUUID());
            entity.setName(status.name());
            entity.setDescription(status.getDescription() != null ? status.getDescription() : "description base");
            memberStatusRepository.save(entity);
            eventPublisher.publishEvent(new MemberStatusCatalogUpsertedEvent(
                    entity.getId(), entity.getName(), entity.getDescription()));
        }
    }
}
