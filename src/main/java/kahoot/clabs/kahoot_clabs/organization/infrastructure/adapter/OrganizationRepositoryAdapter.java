package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper.OrganizationMemberPersistenceMapper;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper.OrganizationPersistenceMapper;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.OrganizationJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.OrganizationMemberJpaRepository;

@Repository
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final OrganizationJpaRepository jpaRepository;
    private final OrganizationMemberJpaRepository memberJpaRepository;

    public OrganizationRepositoryAdapter(
            OrganizationJpaRepository jpaRepository,
            OrganizationMemberJpaRepository memberJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationEntity saved = jpaRepository.save(OrganizationPersistenceMapper.toEntity(organization));
        List<OrganizationMember> members = organization.getMembers();
        syncMembers(organization.getId(), members);
        return OrganizationPersistenceMapper.toDomain(saved, members);
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toAggregate);
    }

    @Override
    public Optional<Organization> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toAggregate);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public void delete(Organization organization) {
        memberJpaRepository.deleteByOrganizationId(organization.getId());
        jpaRepository.deleteById(organization.getId());
    }

    private Organization toAggregate(OrganizationEntity entity) {
        List<OrganizationMember> members = memberJpaRepository.findByOrganizationId(entity.getId()).stream()
                .map(OrganizationMemberPersistenceMapper::toDomain)
                .toList();
        return OrganizationPersistenceMapper.toDomain(entity, members);
    }

    /**
     * Members removed from the aggregate are deleted; the remaining ones are inserted or updated.
     */
    private void syncMembers(UUID organizationId, List<OrganizationMember> members) {
        List<UUID> currentIds = members.stream().map(OrganizationMember::getId).toList();
        if (currentIds.isEmpty()) {
            memberJpaRepository.deleteByOrganizationId(organizationId);
            return;
        }
        memberJpaRepository.deleteByOrganizationIdAndIdNotIn(organizationId, currentIds);
        memberJpaRepository.saveAll(members.stream()
                .map(OrganizationMemberPersistenceMapper::toEntity)
                .toList());
    }
}
