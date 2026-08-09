package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogReadPort;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationDepartmentJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationJobJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationMemberStatusCatalogJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationStatusCatalogJpaRepository;

/**
 * Test-profile catalog read/projection adapter (JPA only).
 */
@Repository
@Profile("test")
public class JpaOrganizationCatalogAdapter
        implements OrganizationCatalogReadPort, OrganizationCatalogProjectionPort {

    private final OrganizationDepartmentJpaRepository departmentJpaRepository;
    private final OrganizationJobJpaRepository jobJpaRepository;
    private final OrganizationStatusCatalogJpaRepository statusJpaRepository;
    private final OrganizationMemberStatusCatalogJpaRepository memberStatusJpaRepository;

    public JpaOrganizationCatalogAdapter(
            OrganizationDepartmentJpaRepository departmentJpaRepository,
            OrganizationJobJpaRepository jobJpaRepository,
            OrganizationStatusCatalogJpaRepository statusJpaRepository,
            OrganizationMemberStatusCatalogJpaRepository memberStatusJpaRepository) {
        this.departmentJpaRepository = departmentJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
        this.statusJpaRepository = statusJpaRepository;
        this.memberStatusJpaRepository = memberStatusJpaRepository;
    }

    @Override
    public Optional<CatalogEntryView> findDepartmentByName(String name) {
        return departmentJpaRepository.findAll().stream()
                .filter(entity -> entity.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(entity -> new CatalogEntryView(entity.getId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public Optional<CatalogEntryView> findJobByName(String name) {
        return jobJpaRepository.findAll().stream()
                .filter(entity -> entity.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(entity -> new CatalogEntryView(entity.getId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public Optional<CatalogEntryView> findOrganizationStatusByName(String name) {
        return statusJpaRepository.findAll().stream()
                .filter(entity -> entity.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(entity -> new CatalogEntryView(entity.getId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public Optional<CatalogEntryView> findMemberStatusByName(String name) {
        return memberStatusJpaRepository.findAll().stream()
                .filter(entity -> entity.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(entity -> new CatalogEntryView(entity.getId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public void saveDepartment(UUID id, String name, String description) {
        // no-op in tests
    }

    @Override
    public void saveJob(UUID id, String name, String description) {
        // no-op in tests
    }

    @Override
    public void saveOrganizationStatus(UUID id, String name, String description) {
        // no-op in tests
    }

    @Override
    public void saveMemberStatus(UUID id, String name, String description) {
        // no-op in tests
    }
}
