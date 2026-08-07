package kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationReadDocument;

public interface OrganizationMongoRepository extends MongoRepository<OrganizationReadDocument, UUID> {

    Optional<OrganizationReadDocument> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("{ 'members.userId': ?0 }")
    List<OrganizationReadDocument> findByMemberUserId(UUID userId);

    @Query("{ 'id': ?0, 'members.userId': ?1 }")
    Optional<OrganizationReadDocument> findByIdAndMemberUserId(UUID organizationId, UUID userId);
}
