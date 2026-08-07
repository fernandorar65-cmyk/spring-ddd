package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationDepartmentReadDocument;

public interface OrganizationDepartmentMongoRepository
        extends MongoRepository<OrganizationDepartmentReadDocument, UUID> {

    Optional<OrganizationDepartmentReadDocument> findByName(String name);

    boolean existsByName(String name);
}
