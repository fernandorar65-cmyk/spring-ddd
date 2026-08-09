package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.port.RoleProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.port.RoleReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.PermissionReadModel;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.RoleReadModel;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.PermissionDocument;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.RoleDocument;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.RolePermissionDocument;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo.PermissionMongoRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo.RoleMongoRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo.RolePermissionMongoRepository;

@Repository
@Profile("!test")
public class MongoRoleReadAdapter implements RoleReadPort, RoleProjectionPort {

    private final RoleMongoRepository roleMongoRepository;
    private final PermissionMongoRepository permissionMongoRepository;
    private final RolePermissionMongoRepository rolePermissionMongoRepository;

    public MongoRoleReadAdapter(
            RoleMongoRepository roleMongoRepository,
            PermissionMongoRepository permissionMongoRepository,
            RolePermissionMongoRepository rolePermissionMongoRepository) {
        this.roleMongoRepository = roleMongoRepository;
        this.permissionMongoRepository = permissionMongoRepository;
        this.rolePermissionMongoRepository = rolePermissionMongoRepository;
    }

    @Override
    public Optional<RoleReadModel> findById(UUID id) {
        return roleMongoRepository.findById(id).map(this::toRoleReadModel);
    }

    @Override
    public Optional<RoleReadModel> findByType(String type) {
        return roleMongoRepository.findByType(type).map(this::toRoleReadModel);
    }

    @Override
    public List<PermissionReadModel> findPermissionsByRoleId(UUID roleId) {
        List<UUID> permissionIds = rolePermissionMongoRepository.findByRoleId(roleId).stream()
                .map(RolePermissionDocument::getPermissionId)
                .toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        return permissionMongoRepository.findAllById(permissionIds).stream()
                .map(this::toPermissionReadModel)
                .toList();
    }

    @Override
    public void saveRole(RoleReadModel readModel) {
        roleMongoRepository.save(toRoleDocument(readModel));
        rolePermissionMongoRepository.deleteByRoleId(readModel.id());
        List<RolePermissionDocument> links = readModel.permissionIds().stream()
                .map(permissionId -> {
                    RolePermissionDocument link = new RolePermissionDocument();
                    link.setId(RolePermissionDocument.composeId(readModel.id(), permissionId));
                    link.setRoleId(readModel.id());
                    link.setPermissionId(permissionId);
                    return link;
                })
                .toList();
        if (!links.isEmpty()) {
            rolePermissionMongoRepository.saveAll(links);
        }
    }

    @Override
    public void deleteRoleById(UUID id) {
        rolePermissionMongoRepository.deleteByRoleId(id);
        roleMongoRepository.deleteById(id);
    }

    @Override
    public void savePermission(PermissionReadModel readModel) {
        permissionMongoRepository.save(toPermissionDocument(readModel));
    }

    @Override
    public void deletePermissionById(UUID id) {
        permissionMongoRepository.deleteById(id);
    }

    private RoleReadModel toRoleReadModel(RoleDocument document) {
        List<UUID> permissionIds = rolePermissionMongoRepository.findByRoleId(document.getId()).stream()
                .map(RolePermissionDocument::getPermissionId)
                .toList();
        return new RoleReadModel(
                document.getId(),
                document.getName(),
                document.getType(),
                document.getDescription(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                permissionIds);
    }

    private RoleDocument toRoleDocument(RoleReadModel readModel) {
        RoleDocument document = new RoleDocument();
        document.setId(readModel.id());
        document.setName(readModel.name());
        document.setType(readModel.type());
        document.setDescription(readModel.description());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        return document;
    }

    private PermissionDocument toPermissionDocument(PermissionReadModel readModel) {
        PermissionDocument document = new PermissionDocument();
        document.setId(readModel.id());
        document.setName(readModel.name());
        document.setDescription(readModel.description());
        document.setModule(readModel.module());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        return document;
    }

    private PermissionReadModel toPermissionReadModel(PermissionDocument document) {
        return new PermissionReadModel(
                document.getId(),
                document.getName(),
                document.getDescription(),
                document.getModule(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
