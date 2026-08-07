package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.GameSessionReadDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringGameSessionMongoRepository;

@Repository
public class MongoGameSessionAdapter implements GameSessionReadModelPort {

    private final SpringGameSessionMongoRepository mongoRepository;

    public MongoGameSessionAdapter(SpringGameSessionMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<GameSessionReadModel> findById(UUID id) {
        return mongoRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationId(UUID organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status) {
        return mongoRepository.findByOrganizationIdAndStatus(organizationId, status).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId) {
        return mongoRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByQuizId(UUID quizId) {
        return mongoRepository.findByQuizId(quizId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndId(UUID organizationId, UUID id) {
        return mongoRepository.existsByOrganizationIdAndId(organizationId, id);
    }

    @Override
    public void save(GameSessionReadModel readModel) {
        mongoRepository.save(toDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        mongoRepository.deleteById(id);
    }

    private GameSessionReadModel toReadModel(GameSessionReadDocument document) {
        return new GameSessionReadModel(
                document.getId(),
                document.getOrganizationId(),
                document.getQuizId(),
                document.getHostUserId(),
                document.getStatus(),
                document.getCurrentQuestionIndex(),
                document.getPlayerCount(),
                document.getQuestionCount(),
                document.getStartedAt(),
                document.getFinishedAt(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }

    private GameSessionReadDocument toDocument(GameSessionReadModel readModel) {
        GameSessionReadDocument document = new GameSessionReadDocument();
        document.setId(readModel.id());
        document.setOrganizationId(readModel.organizationId());
        document.setQuizId(readModel.quizId());
        document.setHostUserId(readModel.hostUserId());
        document.setStatus(readModel.status());
        document.setCurrentQuestionIndex(readModel.currentQuestionIndex());
        document.setPlayerCount(readModel.playerCount());
        document.setQuestionCount(readModel.questionCount());
        document.setStartedAt(readModel.startedAt());
        document.setFinishedAt(readModel.finishedAt());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        return document;
    }
}
