package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationMemberEntity;

public interface OrganizationMemberJpaRepository extends JpaRepository<OrganizationMemberEntity, UUID> {

    List<OrganizationMemberEntity> findByOrganizationId(UUID organizationId);

    List<OrganizationMemberEntity> findByUserId(UUID userId);

    Optional<OrganizationMemberEntity> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    void deleteByOrganizationId(UUID organizationId);

    void deleteByOrganizationIdAndIdNotIn(UUID organizationId, Collection<UUID> ids);
}
