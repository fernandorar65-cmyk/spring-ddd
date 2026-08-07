package kahoot.clabs.kahoot_clabs.organization.application.port;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModel;

/**
 * Port for synchronizing organization read models after write-side changes.
 */
public interface OrganizationProjectionPort {

    void save(OrganizationReadModel readModel);

    void deleteById(UUID id);
}
