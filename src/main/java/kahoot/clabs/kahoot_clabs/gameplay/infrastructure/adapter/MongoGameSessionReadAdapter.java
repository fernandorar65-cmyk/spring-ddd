package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.GameSessionDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.PlayerAnswerDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.SessionAnswerOptionDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.SessionPlayerDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.SessionQuestionDocument;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringGameSessionMongoRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringPlayerAnswerMongoRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringSessionAnswerOptionMongoRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringSessionPlayerMongoRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo.SpringSessionQuestionMongoRepository;

@Repository
@Profile("!test")
public class MongoGameSessionReadAdapter implements GameSessionReadModelPort {

    private final SpringGameSessionMongoRepository sessionMongoRepository;
    private final SpringSessionPlayerMongoRepository playerMongoRepository;
    private final SpringSessionQuestionMongoRepository questionMongoRepository;
    private final SpringSessionAnswerOptionMongoRepository optionMongoRepository;
    private final SpringPlayerAnswerMongoRepository answerMongoRepository;

    public MongoGameSessionReadAdapter(
            SpringGameSessionMongoRepository sessionMongoRepository,
            SpringSessionPlayerMongoRepository playerMongoRepository,
            SpringSessionQuestionMongoRepository questionMongoRepository,
            SpringSessionAnswerOptionMongoRepository optionMongoRepository,
            SpringPlayerAnswerMongoRepository answerMongoRepository) {
        this.sessionMongoRepository = sessionMongoRepository;
        this.playerMongoRepository = playerMongoRepository;
        this.questionMongoRepository = questionMongoRepository;
        this.optionMongoRepository = optionMongoRepository;
        this.answerMongoRepository = answerMongoRepository;
    }

    @Override
    public Optional<GameSessionReadModel> findById(UUID id) {
        return sessionMongoRepository.findById(id).map(this::toFullReadModel);
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationId(UUID organizationId) {
        return sessionMongoRepository.findByOrganizationId(organizationId).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status) {
        return sessionMongoRepository.findByOrganizationIdAndStatus(organizationId, status).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId) {
        return sessionMongoRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> search(UUID organizationId, Collection<String> statuses, UUID quizId) {
        boolean hasStatuses = statuses != null && !statuses.isEmpty();
        List<GameSessionDocument> documents;
        if (quizId != null && hasStatuses) {
            documents = sessionMongoRepository.findByOrganizationIdAndQuizIdAndStatusInOrderByCreatedAtDesc(
                    organizationId, quizId, statuses);
        } else if (quizId != null) {
            documents = sessionMongoRepository.findByOrganizationIdAndQuizIdOrderByCreatedAtDesc(
                    organizationId, quizId);
        } else if (hasStatuses) {
            documents = sessionMongoRepository.findByOrganizationIdAndStatusInOrderByCreatedAtDesc(
                    organizationId, statuses);
        } else {
            documents = sessionMongoRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId);
        }
        return documents.stream().map(this::toSummaryReadModel).toList();
    }

    @Override
    public List<GameSessionReadModel> findByQuizId(UUID quizId) {
        return sessionMongoRepository.findByQuizId(quizId).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndId(UUID organizationId, UUID id) {
        return sessionMongoRepository.existsByOrganizationIdAndId(organizationId, id);
    }

    @Override
    public void save(GameSessionReadModel readModel) {
        UUID sessionId = readModel.id();
        replaceChildren(sessionId, readModel);
        sessionMongoRepository.save(toSessionDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        deleteChildren(id);
        sessionMongoRepository.deleteById(id);
    }

    private void replaceChildren(UUID sessionId, GameSessionReadModel readModel) {
        deleteChildren(sessionId);

        List<SessionPlayerDocument> players = readModel.players().stream()
                .map(player -> toPlayerDocument(sessionId, player))
                .toList();
        if (!players.isEmpty()) {
            playerMongoRepository.saveAll(players);
        }

        List<SessionQuestionDocument> questions = readModel.questions().stream()
                .map(question -> toQuestionDocument(sessionId, question))
                .toList();
        if (!questions.isEmpty()) {
            questionMongoRepository.saveAll(questions);
        }

        List<SessionAnswerOptionDocument> options = readModel.questions().stream()
                .flatMap(question -> question.options().stream()
                        .map(option -> toOptionDocument(question.id(), option)))
                .toList();
        if (!options.isEmpty()) {
            optionMongoRepository.saveAll(options);
        }

        List<PlayerAnswerDocument> answers = readModel.answers().stream()
                .map(this::toAnswerDocument)
                .toList();
        if (!answers.isEmpty()) {
            answerMongoRepository.saveAll(answers);
        }
    }

    private void deleteChildren(UUID sessionId) {
        List<SessionPlayerDocument> existingPlayers = playerMongoRepository.findBySessionId(sessionId);
        List<UUID> playerIds = existingPlayers.stream().map(SessionPlayerDocument::getId).toList();
        if (!playerIds.isEmpty()) {
            answerMongoRepository.deleteBySessionPlayerIdIn(playerIds);
        }

        List<SessionQuestionDocument> existingQuestions = questionMongoRepository.findBySessionId(sessionId);
        List<UUID> questionIds = existingQuestions.stream().map(SessionQuestionDocument::getId).toList();
        if (!questionIds.isEmpty()) {
            answerMongoRepository.deleteBySessionQuestionIdIn(questionIds);
            optionMongoRepository.deleteBySessionQuestionIdIn(questionIds);
        }

        playerMongoRepository.deleteBySessionId(sessionId);
        questionMongoRepository.deleteBySessionId(sessionId);
    }

    private GameSessionReadModel toFullReadModel(GameSessionDocument session) {
        UUID sessionId = session.getId();
        List<SessionPlayerDocument> players = playerMongoRepository.findBySessionId(sessionId);
        List<SessionQuestionDocument> questions = questionMongoRepository.findBySessionId(sessionId).stream()
                .sorted(Comparator.comparingInt(SessionQuestionDocument::getOrderIndex))
                .toList();
        List<UUID> questionIds = questions.stream().map(SessionQuestionDocument::getId).toList();
        Map<UUID, List<SessionAnswerOptionDocument>> optionsByQuestion = questionIds.isEmpty()
                ? Map.of()
                : optionMongoRepository.findBySessionQuestionIdIn(questionIds).stream()
                        .collect(Collectors.groupingBy(SessionAnswerOptionDocument::getSessionQuestionId));

        List<UUID> playerIds = players.stream().map(SessionPlayerDocument::getId).toList();
        List<PlayerAnswerDocument> answers = playerIds.isEmpty()
                ? List.of()
                : answerMongoRepository.findBySessionPlayerIdIn(playerIds);

        return new GameSessionReadModel(
                session.getId(),
                session.getOrganizationId(),
                session.getQuizId(),
                session.getHostUserId(),
                session.getStatus(),
                session.getCurrentQuestionIndex(),
                session.getPlayerCount(),
                session.getQuestionCount(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                players.stream().map(this::toPlayerRead).toList(),
                questions.stream()
                        .map(question -> toQuestionRead(
                                question,
                                optionsByQuestion.getOrDefault(question.getId(), List.of())))
                        .toList(),
                answers.stream().map(this::toAnswerRead).toList());
    }

    private GameSessionReadModel toSummaryReadModel(GameSessionDocument session) {
        return new GameSessionReadModel(
                session.getId(),
                session.getOrganizationId(),
                session.getQuizId(),
                session.getHostUserId(),
                session.getStatus(),
                session.getCurrentQuestionIndex(),
                session.getPlayerCount(),
                session.getQuestionCount(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                List.of(),
                List.of(),
                List.of());
    }

    private GameSessionDocument toSessionDocument(GameSessionReadModel readModel) {
        GameSessionDocument document = new GameSessionDocument();
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

    private SessionPlayerDocument toPlayerDocument(UUID sessionId, GameSessionReadModel.PlayerRead player) {
        SessionPlayerDocument document = new SessionPlayerDocument();
        document.setId(player.id());
        document.setSessionId(sessionId);
        document.setUserId(player.userId());
        document.setNickname(player.nickname());
        document.setScore(player.score());
        document.setConnected(player.connected());
        document.setJoinedAt(player.joinedAt());
        return document;
    }

    private SessionQuestionDocument toQuestionDocument(
            UUID sessionId, GameSessionReadModel.QuestionRead question) {
        SessionQuestionDocument document = new SessionQuestionDocument();
        document.setId(question.id());
        document.setSessionId(sessionId);
        document.setOrderIndex(question.orderIndex());
        document.setPoints(question.points());
        document.setTimeLimitSeconds(question.timeLimitSeconds());
        document.setTitle(question.title());
        document.setDescription(question.description());
        document.setQuestionType(question.questionType());
        document.setOpenedAt(question.openedAt());
        document.setClosedAt(question.closedAt());
        return document;
    }

    private SessionAnswerOptionDocument toOptionDocument(
            UUID sessionQuestionId, GameSessionReadModel.OptionRead option) {
        SessionAnswerOptionDocument document = new SessionAnswerOptionDocument();
        document.setId(option.id());
        document.setSessionQuestionId(sessionQuestionId);
        document.setText(option.text());
        document.setOrderIndex(option.orderIndex());
        document.setCorrect(option.correct());
        return document;
    }

    private PlayerAnswerDocument toAnswerDocument(GameSessionReadModel.AnswerRead answer) {
        PlayerAnswerDocument document = new PlayerAnswerDocument();
        document.setId(answer.id());
        document.setSessionQuestionId(answer.sessionQuestionId());
        document.setSessionPlayerId(answer.sessionPlayerId());
        document.setSessionAnswerOptionId(answer.sessionAnswerOptionId());
        document.setCorrect(answer.correct());
        document.setResponseTimeMs(answer.responseTimeMs());
        document.setAwardedPoints(answer.awardedPoints());
        document.setAnsweredAt(answer.answeredAt());
        return document;
    }

    private GameSessionReadModel.PlayerRead toPlayerRead(SessionPlayerDocument document) {
        return new GameSessionReadModel.PlayerRead(
                document.getId(),
                document.getUserId(),
                document.getNickname(),
                document.getScore(),
                document.isConnected(),
                document.getJoinedAt());
    }

    private GameSessionReadModel.QuestionRead toQuestionRead(
            SessionQuestionDocument question, List<SessionAnswerOptionDocument> options) {
        List<GameSessionReadModel.OptionRead> optionReads = options.stream()
                .sorted(Comparator.comparingInt(SessionAnswerOptionDocument::getOrderIndex))
                .map(option -> new GameSessionReadModel.OptionRead(
                        option.getId(),
                        option.getText(),
                        option.getOrderIndex(),
                        option.isCorrect()))
                .toList();
        return new GameSessionReadModel.QuestionRead(
                question.getId(),
                question.getOrderIndex(),
                question.getPoints(),
                question.getTimeLimitSeconds(),
                question.getTitle(),
                question.getDescription(),
                question.getQuestionType(),
                question.getOpenedAt(),
                question.getClosedAt(),
                optionReads);
    }

    private GameSessionReadModel.AnswerRead toAnswerRead(PlayerAnswerDocument document) {
        return new GameSessionReadModel.AnswerRead(
                document.getId(),
                document.getSessionQuestionId(),
                document.getSessionPlayerId(),
                document.getSessionAnswerOptionId(),
                document.isCorrect(),
                document.getResponseTimeMs(),
                document.getAwardedPoints(),
                document.getAnsweredAt());
    }
}
