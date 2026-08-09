package kahoot.clabs.kahoot_clabs.identity.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kahoot.clabs.kahoot_clabs.identity.application.event.PermissionReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.event.RoleReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.event.RoleReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.event.UserReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.event.UserReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.identity.application.port.RoleProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.port.UserProjectionPort;

/**
 * Async AFTER_COMMIT projection of identity write changes to the read store (Mongo in default profile).
 */
@Component
public class IdentityReadModelProjectionListener {

    private static final Logger log = LoggerFactory.getLogger(IdentityReadModelProjectionListener.class);

    private final UserProjectionPort userProjectionPort;
    private final RoleProjectionPort roleProjectionPort;

    public IdentityReadModelProjectionListener(
            UserProjectionPort userProjectionPort,
            RoleProjectionPort roleProjectionPort) {
        this.userProjectionPort = userProjectionPort;
        this.roleProjectionPort = roleProjectionPort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserUpserted(UserReadModelUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=user action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            userProjectionPort.save(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=user action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserDeleted(UserReadModelDeletedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=user action=delete",
                event.getClass().getSimpleName(),
                event.userId());
        try {
            userProjectionPort.deleteById(event.userId());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=user action=delete",
                    event.getClass().getSimpleName(),
                    event.userId(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRoleUpserted(RoleReadModelUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=role action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            roleProjectionPort.saveRole(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=role action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRoleDeleted(RoleReadModelDeletedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=role action=delete",
                event.getClass().getSimpleName(),
                event.roleId());
        try {
            roleProjectionPort.deleteRoleById(event.roleId());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=role action=delete",
                    event.getClass().getSimpleName(),
                    event.roleId(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPermissionUpserted(PermissionReadModelUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=permission action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            roleProjectionPort.savePermission(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=permission action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }
}
