package kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.users.domain.model.Organization;
import kahoot.clabs.kahoot_clabs.users.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.jpa.OrganizationJpaRepository;
import kahoot.clabs.kahoot_clabs.users.infrastructure.persistence.mapper.OrganizationPersistenceMapper;

@Repository
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final OrganizationJpaRepository jpaRepository;

    public OrganizationRepositoryAdapter(OrganizationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Organization save(Organization organization) {
        return OrganizationPersistenceMapper.toDomain(
                jpaRepository.save(OrganizationPersistenceMapper.toEntity(organization)));
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return jpaRepository.findById(id).map(OrganizationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Organization> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(OrganizationPersistenceMapper::toDomain);
    }

    @Override
    public void delete(Organization organization) {
        jpaRepository.deleteById(organization.getId());
    }
}
