package kahoot.clabs.kahoot_clabs.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;

public interface OrganizationRepository {

    Organization save(Organization organization);

    Optional<Organization> findById(UUID id);

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    void delete(Organization organization);
}
