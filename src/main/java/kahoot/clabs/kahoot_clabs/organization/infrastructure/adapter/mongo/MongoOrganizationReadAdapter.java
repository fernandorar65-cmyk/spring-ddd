package kahoot.clabs.kahoot_clabs.organization.infrastructure.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationReadPort;
import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModel;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo.OrganizationReadDocument;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.mongo.OrganizationMongoRepository;

@Repository
@Profile("!test")
public class MongoOrganizationReadAdapter implements OrganizationReadPort, OrganizationProjectionPort {

    private final OrganizationMongoRepository mongoRepository;

    public MongoOrganizationReadAdapter(OrganizationMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<OrganizationReadModel> findById(UUID id) {
        return mongoRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public Optional<OrganizationReadModel> findBySlug(String slug) {
        return mongoRepository.findBySlug(slug).map(this::toReadModel);
    }

    @Override
    public List<OrganizationReadModel> findByMemberUserId(UUID userId) {
        return mongoRepository.findByMemberUserId(userId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public Optional<OrganizationReadModel> findByIdAndMemberUserId(UUID organizationId, UUID userId) {
        return mongoRepository.findByIdAndMemberUserId(organizationId, userId).map(this::toReadModel);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return mongoRepository.existsBySlug(slug);
    }

    @Override
    public void save(OrganizationReadModel readModel) {
        mongoRepository.save(toDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        mongoRepository.deleteById(id);
    }

    private OrganizationReadModel toReadModel(OrganizationReadDocument document) {
        List<OrganizationReadModel.MemberReadModel> members = document.getMembers() == null
                ? List.of()
                : document.getMembers().stream()
                        .map(member -> new OrganizationReadModel.MemberReadModel(
                                member.getId(),
                                member.getUserId(),
                                member.getRoleId(),
                                member.getStatus(),
                                member.getJoinedAt()))
                        .toList();
        return new OrganizationReadModel(
                document.getId(),
                document.getName(),
                document.getSlug(),
                document.getDescription(),
                document.getLogo(),
                document.getTimezone(),
                document.getLanguage(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                members);
    }

    private OrganizationReadDocument toDocument(OrganizationReadModel readModel) {
        OrganizationReadDocument document = new OrganizationReadDocument();
        document.setId(readModel.id());
        document.setName(readModel.name());
        document.setSlug(readModel.slug());
        document.setDescription(readModel.description());
        document.setLogo(readModel.logo());
        document.setTimezone(readModel.timezone());
        document.setLanguage(readModel.language());
        document.setStatus(readModel.status());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        document.setMembers(readModel.members() == null
                ? List.of()
                : readModel.members().stream().map(member -> {
                    OrganizationReadDocument.MemberEmbedded embedded =
                            new OrganizationReadDocument.MemberEmbedded();
                    embedded.setId(member.id());
                    embedded.setUserId(member.userId());
                    embedded.setRoleId(member.roleId());
                    embedded.setStatus(member.status());
                    embedded.setJoinedAt(member.joinedAt());
                    return embedded;
                }).toList());
        return document;
    }
}
