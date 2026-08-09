package kahoot.clabs.kahoot_clabs.organization.application.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModel;

/**
 * Query-side port for reading organization projections.
 */
public interface OrganizationReadPort {

    Optional<OrganizationReadModel> findById(UUID id);

    Optional<OrganizationReadModel> findBySlug(String slug);

    List<OrganizationReadModel> findByMemberUserId(UUID userId);

    Optional<OrganizationReadModel> findByIdAndMemberUserId(UUID organizationId, UUID userId);

    boolean existsBySlug(String slug);
}
