package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationMemberStatusCatalogReadDocument;

public interface OrganizationMemberStatusCatalogMongoRepository
        extends MongoRepository<OrganizationMemberStatusCatalogReadDocument, UUID> {

    Optional<OrganizationMemberStatusCatalogReadDocument> findByName(String name);

    boolean existsByName(String name);
}
