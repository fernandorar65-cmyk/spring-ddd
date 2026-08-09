package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogReadPort;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationDepartmentReadDocument;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationJobReadDocument;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationMemberStatusCatalogReadDocument;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationStatusCatalogReadDocument;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo.OrganizationDepartmentMongoRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo.OrganizationJobMongoRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo.OrganizationMemberStatusCatalogMongoRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo.OrganizationStatusCatalogMongoRepository;

@Repository
@Profile("!test")
public class MongoOrganizationCatalogAdapter implements OrganizationCatalogReadPort, OrganizationCatalogProjectionPort {

    private final OrganizationDepartmentMongoRepository departmentMongoRepository;
    private final OrganizationJobMongoRepository jobMongoRepository;
    private final OrganizationStatusCatalogMongoRepository statusMongoRepository;
    private final OrganizationMemberStatusCatalogMongoRepository memberStatusMongoRepository;

    public MongoOrganizationCatalogAdapter(
            OrganizationDepartmentMongoRepository departmentMongoRepository,
            OrganizationJobMongoRepository jobMongoRepository,
            OrganizationStatusCatalogMongoRepository statusMongoRepository,
            OrganizationMemberStatusCatalogMongoRepository memberStatusMongoRepository) {
        this.departmentMongoRepository = departmentMongoRepository;
        this.jobMongoRepository = jobMongoRepository;
        this.statusMongoRepository = statusMongoRepository;
        this.memberStatusMongoRepository = memberStatusMongoRepository;
    }

    @Override
    public Optional<CatalogEntryView> findDepartmentByName(String name) {
        return departmentMongoRepository.findByName(name)
                .map(doc -> new CatalogEntryView(doc.getId(), doc.getName(), doc.getDescription()));
    }

    @Override
    public Optional<CatalogEntryView> findJobByName(String name) {
        return jobMongoRepository.findByName(name)
                .map(doc -> new CatalogEntryView(doc.getId(), doc.getName(), doc.getDescription()));
    }

    @Override
    public Optional<CatalogEntryView> findOrganizationStatusByName(String name) {
        return statusMongoRepository.findByName(name)
                .map(doc -> new CatalogEntryView(doc.getId(), doc.getName(), doc.getDescription()));
    }

    @Override
    public Optional<CatalogEntryView> findMemberStatusByName(String name) {
        return memberStatusMongoRepository.findByName(name)
                .map(doc -> new CatalogEntryView(doc.getId(), doc.getName(), doc.getDescription()));
    }

    @Override
    public void saveDepartment(UUID id, String name, String description) {
        OrganizationDepartmentReadDocument document = new OrganizationDepartmentReadDocument();
        document.setId(id);
        document.setName(name);
        document.setDescription(description);
        departmentMongoRepository.save(document);
    }

    @Override
    public void saveJob(UUID id, String name, String description) {
        OrganizationJobReadDocument document = new OrganizationJobReadDocument();
        document.setId(id);
        document.setName(name);
        document.setDescription(description);
        jobMongoRepository.save(document);
    }

    @Override
    public void saveOrganizationStatus(UUID id, String name, String description) {
        OrganizationStatusCatalogReadDocument document = new OrganizationStatusCatalogReadDocument();
        document.setId(id);
        document.setName(name);
        document.setDescription(description);
        statusMongoRepository.save(document);
    }

    @Override
    public void saveMemberStatus(UUID id, String name, String description) {
        OrganizationMemberStatusCatalogReadDocument document = new OrganizationMemberStatusCatalogReadDocument();
        document.setId(id);
        document.setName(name);
        document.setDescription(description);
        memberStatusMongoRepository.save(document);
    }
}
