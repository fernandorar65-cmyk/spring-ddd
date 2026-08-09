package kahoot.clabs.kahoot_clabs.organization.infrastructure.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kahoot.clabs.kahoot_clabs.organization.application.event.DepartmentCatalogUpsertedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.event.JobCatalogUpsertedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.event.MemberStatusCatalogUpsertedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.event.OrganizationReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.event.OrganizationReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.event.OrganizationStatusCatalogUpsertedEvent;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationProjectionPort;

@Component
public class OrganizationReadModelProjectionListener {

    private final OrganizationProjectionPort organizationProjectionPort;
    private final OrganizationCatalogProjectionPort catalogProjectionPort;

    public OrganizationReadModelProjectionListener(
            OrganizationProjectionPort organizationProjectionPort,
            OrganizationCatalogProjectionPort catalogProjectionPort) {
        this.organizationProjectionPort = organizationProjectionPort;
        this.catalogProjectionPort = catalogProjectionPort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrganizationUpserted(OrganizationReadModelUpsertedEvent event) {
        organizationProjectionPort.save(event.readModel());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrganizationDeleted(OrganizationReadModelDeletedEvent event) {
        organizationProjectionPort.deleteById(event.organizationId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDepartmentUpserted(DepartmentCatalogUpsertedEvent event) {
        catalogProjectionPort.saveDepartment(event.id(), event.name(), event.description());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onJobUpserted(JobCatalogUpsertedEvent event) {
        catalogProjectionPort.saveJob(event.id(), event.name(), event.description());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrganizationStatusUpserted(OrganizationStatusCatalogUpsertedEvent event) {
        catalogProjectionPort.saveOrganizationStatus(event.id(), event.name(), event.description());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMemberStatusUpserted(MemberStatusCatalogUpsertedEvent event) {
        catalogProjectionPort.saveMemberStatus(event.id(), event.name(), event.description());
    }
}
