package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationReadPort;
import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModel;
import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModels;
import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper.OrganizationMemberPersistenceMapper;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper.OrganizationPersistenceMapper;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationMemberJpaRepository;

/**
 * Test-profile read/projection adapter: serves queries from Postgres/JPA without Mongo.
 */
@Repository
@Profile("test")
public class JpaOrganizationReadAdapter implements OrganizationReadPort, OrganizationProjectionPort {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationMemberJpaRepository memberJpaRepository;

    public JpaOrganizationReadAdapter(
            OrganizationJpaRepository organizationJpaRepository,
            OrganizationMemberJpaRepository memberJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public Optional<OrganizationReadModel> findById(UUID id) {
        return organizationJpaRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public Optional<OrganizationReadModel> findBySlug(String slug) {
        return organizationJpaRepository.findBySlug(slug).map(this::toReadModel);
    }

    @Override
    public List<OrganizationReadModel> findByMemberUserId(UUID userId) {
        return organizationJpaRepository.findAll().stream()
                .map(this::toReadModel)
                .filter(org -> org.members().stream().anyMatch(member -> member.userId().equals(userId)))
                .toList();
    }

    @Override
    public Optional<OrganizationReadModel> findByIdAndMemberUserId(UUID organizationId, UUID userId) {
        return findById(organizationId)
                .filter(org -> org.members().stream().anyMatch(member -> member.userId().equals(userId)));
    }

    @Override
    public boolean existsBySlug(String slug) {
        return organizationJpaRepository.existsBySlug(slug);
    }

    @Override
    public void save(OrganizationReadModel readModel) {
        // no-op: Postgres remains source of truth in tests
    }

    @Override
    public void deleteById(UUID id) {
        // no-op
    }

    private OrganizationReadModel toReadModel(OrganizationEntity entity) {
        List<OrganizationMember> members = memberJpaRepository.findByOrganizationId(entity.getId()).stream()
                .map(OrganizationMemberPersistenceMapper::toDomain)
                .toList();
        return OrganizationReadModels.from(OrganizationPersistenceMapper.toDomain(entity, members));
    }
}
