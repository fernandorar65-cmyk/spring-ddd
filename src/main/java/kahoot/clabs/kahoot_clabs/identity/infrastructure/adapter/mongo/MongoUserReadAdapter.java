package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.identity.application.port.UserProjectionPort;
import kahoot.clabs.kahoot_clabs.identity.application.port.UserReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.readmodel.UserReadModel;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.UserDocument;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo.UserImageDocument;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo.UserImageMongoRepository;
import kahoot.clabs.kahoot_clabs.identity.infrastructure.repository.mongo.UserMongoRepository;

@Repository
@Profile("!test")
public class MongoUserReadAdapter implements UserReadPort, UserProjectionPort {

    private final UserMongoRepository userMongoRepository;
    private final UserImageMongoRepository userImageMongoRepository;

    public MongoUserReadAdapter(
            UserMongoRepository userMongoRepository,
            UserImageMongoRepository userImageMongoRepository) {
        this.userMongoRepository = userMongoRepository;
        this.userImageMongoRepository = userImageMongoRepository;
    }

    @Override
    public Optional<UserReadModel> findById(UUID id) {
        return userMongoRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public Optional<UserReadModel> findByEmail(String email) {
        return userMongoRepository.findByEmailIgnoreCase(email).map(this::toReadModel);
    }

    @Override
    public void save(UserReadModel readModel) {
        userMongoRepository.save(toUserDocument(readModel));
        userImageMongoRepository.deleteByUserId(readModel.id());
        List<UserImageDocument> images = readModel.images().stream()
                .map(this::toImageDocument)
                .toList();
        if (!images.isEmpty()) {
            userImageMongoRepository.saveAll(images);
        }
    }

    @Override
    public void deleteById(UUID id) {
        userImageMongoRepository.deleteByUserId(id);
        userMongoRepository.deleteById(id);
    }

    private UserReadModel toReadModel(UserDocument document) {
        List<UserReadModel.ImageReadModel> images = userImageMongoRepository.findByUserId(document.getId())
                .stream()
                .map(this::toImageRead)
                .toList();
        return new UserReadModel(
                document.getId(),
                document.getRoleId(),
                document.getEmail(),
                document.getFirstName(),
                document.getLastName(),
                document.getStatus(),
                document.getPhoneNumber(),
                document.getBirthDate(),
                document.getBio(),
                document.getLocation(),
                document.getLastLogin(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                images);
    }

    private UserDocument toUserDocument(UserReadModel readModel) {
        UserDocument document = new UserDocument();
        document.setId(readModel.id());
        document.setRoleId(readModel.roleId());
        document.setEmail(readModel.email());
        document.setFirstName(readModel.firstName());
        document.setLastName(readModel.lastName());
        document.setStatus(readModel.status());
        document.setPhoneNumber(readModel.phoneNumber());
        document.setBirthDate(readModel.birthDate());
        document.setBio(readModel.bio());
        document.setLocation(readModel.location());
        document.setLastLogin(readModel.lastLogin());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        return document;
    }

    private UserImageDocument toImageDocument(UserReadModel.ImageReadModel image) {
        UserImageDocument document = new UserImageDocument();
        document.setId(image.id());
        document.setUserId(image.userId());
        document.setUrl(image.url());
        document.setType(image.type());
        document.setAlt(image.alt());
        document.setSlug(image.slug());
        document.setCreatedAt(image.createdAt());
        document.setUpdatedAt(image.updatedAt());
        return document;
    }

    private UserReadModel.ImageReadModel toImageRead(UserImageDocument document) {
        return new UserReadModel.ImageReadModel(
                document.getId(),
                document.getUserId(),
                document.getUrl(),
                document.getType(),
                document.getAlt(),
                document.getSlug(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
