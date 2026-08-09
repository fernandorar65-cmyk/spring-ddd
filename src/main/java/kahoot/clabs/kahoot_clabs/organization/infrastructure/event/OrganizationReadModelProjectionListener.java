package kahoot.clabs.kahoot_clabs.organization.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(OrganizationReadModelProjectionListener.class);

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
        log.info(
                "projecting eventType={} aggregateId={} projectionType=organization action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            organizationProjectionPort.save(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=organization action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrganizationDeleted(OrganizationReadModelDeletedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=organization action=delete",
                event.getClass().getSimpleName(),
                event.organizationId());
        try {
            organizationProjectionPort.deleteById(event.organizationId());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=organization action=delete",
                    event.getClass().getSimpleName(),
                    event.organizationId(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDepartmentUpserted(DepartmentCatalogUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=departmentCatalog action=upsert",
                event.getClass().getSimpleName(),
                event.id());
        try {
            catalogProjectionPort.saveDepartment(event.id(), event.name(), event.description());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=departmentCatalog action=upsert",
                    event.getClass().getSimpleName(),
                    event.id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onJobUpserted(JobCatalogUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=jobCatalog action=upsert",
                event.getClass().getSimpleName(),
                event.id());
        try {
            catalogProjectionPort.saveJob(event.id(), event.name(), event.description());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=jobCatalog action=upsert",
                    event.getClass().getSimpleName(),
                    event.id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrganizationStatusUpserted(OrganizationStatusCatalogUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=organizationStatusCatalog action=upsert",
                event.getClass().getSimpleName(),
                event.id());
        try {
            catalogProjectionPort.saveOrganizationStatus(event.id(), event.name(), event.description());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=organizationStatusCatalog action=upsert",
                    event.getClass().getSimpleName(),
                    event.id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMemberStatusUpserted(MemberStatusCatalogUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=memberStatusCatalog action=upsert",
                event.getClass().getSimpleName(),
                event.id());
        try {
            catalogProjectionPort.saveMemberStatus(event.id(), event.name(), event.description());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=memberStatusCatalog action=upsert",
                    event.getClass().getSimpleName(),
                    event.id(),
                    ex);
            throw ex;
        }
    }
}
