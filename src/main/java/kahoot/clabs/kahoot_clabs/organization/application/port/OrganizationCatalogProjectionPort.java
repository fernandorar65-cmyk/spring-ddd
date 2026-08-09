package kahoot.clabs.kahoot_clabs.organization.application.port;

import java.util.UUID;

/**
 * Port for projecting organization catalog reference data to the read store.
 */
public interface OrganizationCatalogProjectionPort {

    void saveDepartment(UUID id, String name, String description);

    void saveJob(UUID id, String name, String description);

    void saveOrganizationStatus(UUID id, String name, String description);

    void saveMemberStatus(UUID id, String name, String description);
}
