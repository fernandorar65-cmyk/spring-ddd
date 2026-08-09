package kahoot.clabs.kahoot_clabs.identity.infrastructure.event;

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
        userProjectionPort.save(event.readModel());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserDeleted(UserReadModelDeletedEvent event) {
        userProjectionPort.deleteById(event.userId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRoleUpserted(RoleReadModelUpsertedEvent event) {
        roleProjectionPort.saveRole(event.readModel());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRoleDeleted(RoleReadModelDeletedEvent event) {
        roleProjectionPort.deleteRoleById(event.roleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPermissionUpserted(PermissionReadModelUpsertedEvent event) {
        roleProjectionPort.savePermission(event.readModel());
    }
}
