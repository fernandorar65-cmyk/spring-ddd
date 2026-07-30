package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationMemberStatusCatalogEntity;

public interface OrganizationMemberStatusCatalogJpaRepository
        extends JpaRepository<OrganizationMemberStatusCatalogEntity, UUID> {

    Optional<OrganizationMemberStatusCatalogEntity> findByName(String name);

    boolean existsByName(String name);
}
