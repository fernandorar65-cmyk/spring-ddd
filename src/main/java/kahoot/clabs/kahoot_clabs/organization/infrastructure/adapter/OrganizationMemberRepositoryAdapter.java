package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationMemberRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.mapper.OrganizationMemberPersistenceMapper;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.OrganizationMemberJpaRepository;

@Repository
public class OrganizationMemberRepositoryAdapter implements OrganizationMemberRepository {

    private final OrganizationMemberJpaRepository jpaRepository;

    public OrganizationMemberRepositoryAdapter(OrganizationMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<OrganizationMember> findByOrganizationId(UUID organizationId) {
        return jpaRepository.findByOrganizationId(organizationId).stream()
                .map(OrganizationMemberPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<OrganizationMember> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(OrganizationMemberPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<OrganizationMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return jpaRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .map(OrganizationMemberPersistenceMapper::toDomain);
    }
}
