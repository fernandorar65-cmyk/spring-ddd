package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.GameSessionReadDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringGameSessionMongoRepository;

@Repository
@Profile("!test")
public class MongoGameSessionReadAdapter implements GameSessionReadModelPort {

    private final SpringGameSessionMongoRepository mongoRepository;

    public MongoGameSessionReadAdapter(SpringGameSessionMongoRepository mongoRepository) {
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
    public List<GameSessionReadModel> search(UUID organizationId, Collection<String> statuses, UUID quizId) {
        boolean hasStatuses = statuses != null && !statuses.isEmpty();
        List<GameSessionReadDocument> documents;
        if (quizId != null && hasStatuses) {
            documents = mongoRepository.findByOrganizationIdAndQuizIdAndStatusInOrderByCreatedAtDesc(
                    organizationId, quizId, statuses);
        } else if (quizId != null) {
            documents = mongoRepository.findByOrganizationIdAndQuizIdOrderByCreatedAtDesc(
                    organizationId, quizId);
        } else if (hasStatuses) {
            documents = mongoRepository.findByOrganizationIdAndStatusInOrderByCreatedAtDesc(
                    organizationId, statuses);
        } else {
            documents = mongoRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId);
        }
        return documents.stream().map(this::toReadModel).toList();
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
        List<GameSessionReadModel.PlayerRead> players = document.getPlayers() == null
                ? List.of()
                : document.getPlayers().stream().map(this::toPlayer).toList();
        List<GameSessionReadModel.QuestionRead> questions = document.getQuestions() == null
                ? List.of()
                : document.getQuestions().stream().map(this::toQuestion).toList();
        List<GameSessionReadModel.AnswerRead> answers = document.getAnswers() == null
                ? List.of()
                : document.getAnswers().stream().map(this::toAnswer).toList();
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
                document.getUpdatedAt(),
                players,
                questions,
                answers);
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
        document.setPlayers(readModel.players().stream().map(this::toPlayerEmbedded).toList());
        document.setQuestions(readModel.questions().stream().map(this::toQuestionEmbedded).toList());
        document.setAnswers(readModel.answers().stream().map(this::toAnswerEmbedded).toList());
        return document;
    }

    // validar si lo paso a la parte del dominio y no en el adaptador

    private GameSessionReadModel.PlayerRead toPlayer(GameSessionReadDocument.PlayerEmbedded embedded) {
        return new GameSessionReadModel.PlayerRead(
                embedded.getId(),
                embedded.getUserId(),
                embedded.getNickname(),
                embedded.getScore(),
                embedded.isConnected(),
                embedded.getJoinedAt());
    }

    private GameSessionReadModel.QuestionRead toQuestion(GameSessionReadDocument.QuestionEmbedded embedded) {
        List<GameSessionReadModel.OptionRead> options = embedded.getOptions() == null
                ? List.of()
                : embedded.getOptions().stream()
                        .map(option -> new GameSessionReadModel.OptionRead(
                                option.getId(),
                                option.getText(),
                                option.getOrderIndex(),
                                option.isCorrect()))
                        .toList();
        return new GameSessionReadModel.QuestionRead(
                embedded.getId(),
                embedded.getOrderIndex(),
                embedded.getPoints(),
                embedded.getTimeLimitSeconds(),
                embedded.getTitle(),
                embedded.getDescription(),
                embedded.getQuestionType(),
                embedded.getOpenedAt(),
                embedded.getClosedAt(),
                options);
    }

    private GameSessionReadModel.AnswerRead toAnswer(GameSessionReadDocument.AnswerEmbedded embedded) {
        return new GameSessionReadModel.AnswerRead(
                embedded.getId(),
                embedded.getSessionQuestionId(),
                embedded.getSessionPlayerId(),
                embedded.getSessionAnswerOptionId(),
                embedded.isCorrect(),
                embedded.getResponseTimeMs(),
                embedded.getAwardedPoints(),
                embedded.getAnsweredAt());
    }

    private GameSessionReadDocument.PlayerEmbedded toPlayerEmbedded(GameSessionReadModel.PlayerRead player) {
        GameSessionReadDocument.PlayerEmbedded embedded = new GameSessionReadDocument.PlayerEmbedded();
        embedded.setId(player.id());
        embedded.setUserId(player.userId());
        embedded.setNickname(player.nickname());
        embedded.setScore(player.score());
        embedded.setConnected(player.connected());
        embedded.setJoinedAt(player.joinedAt());
        return embedded;
    }

    private GameSessionReadDocument.QuestionEmbedded toQuestionEmbedded(GameSessionReadModel.QuestionRead question) {
        GameSessionReadDocument.QuestionEmbedded embedded = new GameSessionReadDocument.QuestionEmbedded();
        embedded.setId(question.id());
        embedded.setOrderIndex(question.orderIndex());
        embedded.setPoints(question.points());
        embedded.setTimeLimitSeconds(question.timeLimitSeconds());
        embedded.setTitle(question.title());
        embedded.setDescription(question.description());
        embedded.setQuestionType(question.questionType());
        embedded.setOpenedAt(question.openedAt());
        embedded.setClosedAt(question.closedAt());
        embedded.setOptions(question.options().stream().map(this::toOptionEmbedded).toList());
        return embedded;
    }

    private GameSessionReadDocument.OptionEmbedded toOptionEmbedded(GameSessionReadModel.OptionRead option) {
        GameSessionReadDocument.OptionEmbedded embedded = new GameSessionReadDocument.OptionEmbedded();
        embedded.setId(option.id());
        embedded.setText(option.text());
        embedded.setOrderIndex(option.orderIndex());
        embedded.setCorrect(option.correct());
        return embedded;
    }

    private GameSessionReadDocument.AnswerEmbedded toAnswerEmbedded(GameSessionReadModel.AnswerRead answer) {
        GameSessionReadDocument.AnswerEmbedded embedded = new GameSessionReadDocument.AnswerEmbedded();
        embedded.setId(answer.id());
        embedded.setSessionQuestionId(answer.sessionQuestionId());
        embedded.setSessionPlayerId(answer.sessionPlayerId());
        embedded.setSessionAnswerOptionId(answer.sessionAnswerOptionId());
        embedded.setCorrect(answer.correct());
        embedded.setResponseTimeMs(answer.responseTimeMs());
        embedded.setAwardedPoints(answer.awardedPoints());
        embedded.setAnsweredAt(answer.answeredAt());
        return embedded;
    }
}
