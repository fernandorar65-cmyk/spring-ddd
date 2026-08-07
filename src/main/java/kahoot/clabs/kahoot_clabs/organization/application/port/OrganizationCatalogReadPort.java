package kahoot.clabs.kahoot_clabs.organization.application.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Query-side port for organization reference catalogs (departments, jobs, statuses).
 */
public interface OrganizationCatalogReadPort {

    Optional<CatalogEntryView> findDepartmentByName(String name);

    Optional<CatalogEntryView> findJobByName(String name);

    Optional<CatalogEntryView> findOrganizationStatusByName(String name);

    Optional<CatalogEntryView> findMemberStatusByName(String name);

    record CatalogEntryView(UUID id, String name, String description) {
    }
}
