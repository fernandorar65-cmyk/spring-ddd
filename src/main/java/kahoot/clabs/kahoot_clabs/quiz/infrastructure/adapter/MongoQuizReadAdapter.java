package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizReadModelPort;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizReadDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringDataQuizReadRepository;

@Repository
@Profile("!test")
public class MongoQuizReadAdapter implements QuizReadModelPort {

    private final SpringDataQuizReadRepository mongoRepository;

    public MongoQuizReadAdapter(SpringDataQuizReadRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<QuizReadModel> findById(UUID id) {
        return mongoRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public List<QuizReadModel> findByOrganizationId(UUID organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<QuizReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status) {
        return mongoRepository.findByOrganizationIdAndStatus(organizationId, status).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<QuizReadModel> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId) {
        return mongoRepository.findByOrganizationIdOrderByUpdatedAtDesc(organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndId(UUID organizationId, UUID id) {
        return mongoRepository.existsByOrganizationIdAndId(organizationId, id);
    }

    @Override
    public void save(QuizReadModel readModel) {
        mongoRepository.save(toDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        mongoRepository.deleteById(id);
    }

    private QuizReadModel toReadModel(QuizReadDocument document) {
        return new QuizReadModel(
                document.getId(),
                document.getOrganizationId(),
                document.getCreatedById(),
                document.getTitle(),
                document.getDescription(),
                document.getThumbnail(),
                document.getStatus(),
                document.getDifficulty(),
                document.getEstimatedTimeMinutes(),
                document.getPlayCount(),
                document.getAverageRating(),
                document.isTemplate(),
                document.getCategoryIds() == null ? List.of() : List.copyOf(document.getCategoryIds()),
                document.getQuestionCount(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }

    private QuizReadDocument toDocument(QuizReadModel readModel) {
        QuizReadDocument document = new QuizReadDocument();
        document.setId(readModel.id());
        document.setOrganizationId(readModel.organizationId());
        document.setCreatedById(readModel.createdById());
        document.setTitle(readModel.title());
        document.setDescription(readModel.description());
        document.setThumbnail(readModel.thumbnail());
        document.setStatus(readModel.status());
        document.setDifficulty(readModel.difficulty());
        document.setEstimatedTimeMinutes(readModel.estimatedTimeMinutes());
        document.setPlayCount(readModel.playCount());
        document.setAverageRating(readModel.averageRating());
        document.setTemplate(readModel.template());
        document.setCategoryIds(
                readModel.categoryIds() == null ? List.of() : List.copyOf(readModel.categoryIds()));
        document.setQuestionCount(readModel.questionCount());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        return document;
    }
}
