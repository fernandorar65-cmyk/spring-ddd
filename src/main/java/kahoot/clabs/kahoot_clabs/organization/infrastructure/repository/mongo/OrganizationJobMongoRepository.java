package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationJobReadDocument;

public interface OrganizationJobMongoRepository extends MongoRepository<OrganizationJobReadDocument, UUID> {

    Optional<OrganizationJobReadDocument> findByName(String name);

    boolean existsByName(String name);
}
