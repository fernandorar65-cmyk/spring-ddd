package kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.RolePermissionDocument;

public interface RolePermissionMongoRepository extends MongoRepository<RolePermissionDocument, String> {

    List<RolePermissionDocument> findByRoleId(UUID roleId);

    void deleteByRoleId(UUID roleId);
}
