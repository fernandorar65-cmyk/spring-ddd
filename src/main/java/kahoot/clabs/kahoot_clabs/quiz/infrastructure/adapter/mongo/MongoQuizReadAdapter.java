package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.mongo;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizProjectionPort;
import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizReadPort;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.AnswerOptionDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuestionAssetDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuestionDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizCategoryDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringAnswerOptionMongoRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringQuestionAssetMongoRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringQuestionMongoRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringQuizCategoryMongoRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringQuizMongoRepository;

@Repository
@Profile("!test")
public class MongoQuizReadAdapter implements QuizReadPort, QuizProjectionPort {

    private final SpringQuizMongoRepository quizMongoRepository;
    private final SpringQuestionMongoRepository questionMongoRepository;
    private final SpringAnswerOptionMongoRepository answerOptionMongoRepository;
    private final SpringQuestionAssetMongoRepository questionAssetMongoRepository;
    private final SpringQuizCategoryMongoRepository quizCategoryMongoRepository;

    public MongoQuizReadAdapter(
            SpringQuizMongoRepository quizMongoRepository,
            SpringQuestionMongoRepository questionMongoRepository,
            SpringAnswerOptionMongoRepository answerOptionMongoRepository,
            SpringQuestionAssetMongoRepository questionAssetMongoRepository,
            SpringQuizCategoryMongoRepository quizCategoryMongoRepository) {
        this.quizMongoRepository = quizMongoRepository;
        this.questionMongoRepository = questionMongoRepository;
        this.answerOptionMongoRepository = answerOptionMongoRepository;
        this.questionAssetMongoRepository = questionAssetMongoRepository;
        this.quizCategoryMongoRepository = quizCategoryMongoRepository;
    }

    @Override
    public Optional<QuizReadModel> findById(UUID id) {
        return quizMongoRepository.findById(id).map(this::toFullReadModel);
    }

    @Override
    public List<QuizReadModel> findByOrganizationId(UUID organizationId) {
        return quizMongoRepository.findByOrganizationId(organizationId).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public List<QuizReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status) {
        return quizMongoRepository.findByOrganizationIdAndStatus(organizationId, status).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public List<QuizReadModel> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId) {
        return quizMongoRepository.findByOrganizationIdOrderByUpdatedAtDesc(organizationId).stream()
                .map(this::toSummaryReadModel)
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndId(UUID organizationId, UUID id) {
        return quizMongoRepository.existsByOrganizationIdAndId(organizationId, id);
    }

    @Override
    public void save(QuizReadModel readModel) {
        replaceChildren(readModel);
        quizMongoRepository.save(toQuizDocument(readModel));
    }

    @Override
    public void deleteById(UUID id) {
        deleteChildren(id);
        quizMongoRepository.deleteById(id);
    }

    private void replaceChildren(QuizReadModel readModel) {
        UUID quizId = readModel.id();
        deleteChildren(quizId);

        List<QuizCategoryDocument> categories = readModel.categoryIds().stream()
                .map(categoryId -> {
                    QuizCategoryDocument document = new QuizCategoryDocument();
                    document.setId(QuizCategoryDocument.composeId(quizId, categoryId));
                    document.setQuizId(quizId);
                    document.setCategoryId(categoryId);
                    return document;
                })
                .toList();
        if (!categories.isEmpty()) {
            quizCategoryMongoRepository.saveAll(categories);
        }

        LocalDateTime now = LocalDateTime.now();
        List<QuestionDocument> questions = readModel.questions().stream()
                .map(question -> toQuestionDocument(quizId, question, now))
                .toList();
        if (!questions.isEmpty()) {
            questionMongoRepository.saveAll(questions);
        }

        List<AnswerOptionDocument> options = readModel.questions().stream()
                .flatMap(question -> question.options().stream()
                        .map(option -> toOptionDocument(question.id(), option, now)))
                .toList();
        if (!options.isEmpty()) {
            answerOptionMongoRepository.saveAll(options);
        }

        List<QuestionAssetDocument> assets = readModel.questions().stream()
                .filter(question -> question.asset() != null)
                .map(question -> toAssetDocument(question.id(), question.asset(), now))
                .toList();
        if (!assets.isEmpty()) {
            questionAssetMongoRepository.saveAll(assets);
        }
    }

    private void deleteChildren(UUID quizId) {
        List<QuestionDocument> existingQuestions = questionMongoRepository.findByQuizId(quizId);
        List<UUID> questionIds = existingQuestions.stream().map(QuestionDocument::getId).toList();
        if (!questionIds.isEmpty()) {
            answerOptionMongoRepository.deleteByQuestionIdIn(questionIds);
            questionAssetMongoRepository.deleteByQuestionIdIn(questionIds);
        }
        questionMongoRepository.deleteByQuizId(quizId);
        quizCategoryMongoRepository.deleteByQuizId(quizId);
    }

    private QuizReadModel toFullReadModel(QuizDocument quiz) {
        UUID quizId = quiz.getId();
        List<UUID> categoryIds = quizCategoryMongoRepository.findByQuizId(quizId).stream()
                .map(QuizCategoryDocument::getCategoryId)
                .toList();
        List<QuestionDocument> questions = questionMongoRepository.findByQuizId(quizId).stream()
                .sorted(Comparator.comparingInt(QuestionDocument::getOrderIndex))
                .toList();
        List<UUID> questionIds = questions.stream().map(QuestionDocument::getId).toList();
        Map<UUID, List<AnswerOptionDocument>> optionsByQuestion = questionIds.isEmpty()
                ? Map.of()
                : answerOptionMongoRepository.findByQuestionIdIn(questionIds).stream()
                        .collect(Collectors.groupingBy(AnswerOptionDocument::getQuestionId));
        Map<UUID, QuestionAssetDocument> assetByQuestion = questionIds.isEmpty()
                ? Map.of()
                : questionAssetMongoRepository.findByQuestionIdIn(questionIds).stream()
                        .collect(Collectors.toMap(QuestionAssetDocument::getQuestionId, Function.identity()));

        List<QuizReadModel.QuestionRead> questionReads = questions.stream()
                .map(question -> toQuestionRead(
                        question,
                        optionsByQuestion.getOrDefault(question.getId(), List.of()),
                        assetByQuestion.get(question.getId())))
                .toList();

        return toReadModel(quiz, categoryIds, questionReads);
    }

    private QuizReadModel toSummaryReadModel(QuizDocument quiz) {
        List<UUID> categoryIds = quizCategoryMongoRepository.findByQuizId(quiz.getId()).stream()
                .map(QuizCategoryDocument::getCategoryId)
                .toList();
        return toReadModel(quiz, categoryIds, List.of());
    }

    private QuizReadModel toReadModel(
            QuizDocument quiz, List<UUID> categoryIds, List<QuizReadModel.QuestionRead> questions) {
        return new QuizReadModel(
                quiz.getId(),
                quiz.getOrganizationId(),
                quiz.getCreatedById(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getThumbnail(),
                quiz.getStatus(),
                quiz.getDifficulty(),
                quiz.getEstimatedTimeMinutes(),
                quiz.getPlayCount(),
                quiz.getAverageRating(),
                quiz.isTemplate(),
                quiz.isRandomQuestions(),
                quiz.isRandomAnswers(),
                quiz.isShowCorrectAnswer(),
                quiz.isShowRanking(),
                quiz.isAllowRetry(),
                quiz.isShowTimer(),
                quiz.isMusicEnabled(),
                categoryIds,
                quiz.getQuestionCount(),
                questions,
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }

    private QuizDocument toQuizDocument(QuizReadModel readModel) {
        QuizDocument document = new QuizDocument();
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
        document.setRandomQuestions(readModel.randomQuestions());
        document.setRandomAnswers(readModel.randomAnswers());
        document.setShowCorrectAnswer(readModel.showCorrectAnswer());
        document.setShowRanking(readModel.showRanking());
        document.setAllowRetry(readModel.allowRetry());
        document.setShowTimer(readModel.showTimer());
        document.setMusicEnabled(readModel.musicEnabled());
        document.setQuestionCount(readModel.questionCount());
        document.setCreatedAt(readModel.createdAt());
        document.setUpdatedAt(readModel.updatedAt());
        return document;
    }

    private QuestionDocument toQuestionDocument(
            UUID quizId, QuizReadModel.QuestionRead question, LocalDateTime now) {
        QuestionDocument document = new QuestionDocument();
        document.setId(question.id());
        document.setQuizId(question.quizId() != null ? question.quizId() : quizId);
        document.setTitle(question.title());
        document.setDescription(question.description());
        document.setType(question.type());
        document.setDifficulty(question.difficulty());
        document.setExplanation(question.explanation());
        document.setOrderIndex(question.orderIndex());
        document.setTimeLimitSeconds(question.timeLimitSeconds());
        document.setPoints(question.points());
        document.setCreatedAt(question.createdAt() != null ? question.createdAt() : now);
        document.setUpdatedAt(question.updatedAt() != null ? question.updatedAt() : now);
        return document;
    }

    private AnswerOptionDocument toOptionDocument(
            UUID questionId, QuizReadModel.OptionRead option, LocalDateTime now) {
        AnswerOptionDocument document = new AnswerOptionDocument();
        document.setId(option.id());
        document.setQuestionId(option.questionId() != null ? option.questionId() : questionId);
        document.setText(option.text());
        document.setCorrect(option.correct());
        document.setExplanation(option.explanation());
        document.setOrderIndex(option.orderIndex());
        document.setCreatedAt(option.createdAt() != null ? option.createdAt() : now);
        document.setUpdatedAt(option.updatedAt() != null ? option.updatedAt() : now);
        return document;
    }

    private QuestionAssetDocument toAssetDocument(
            UUID questionId, QuizReadModel.AssetRead asset, LocalDateTime now) {
        QuestionAssetDocument document = new QuestionAssetDocument();
        document.setId(asset.id());
        document.setQuestionId(asset.questionId() != null ? asset.questionId() : questionId);
        document.setType(asset.type());
        document.setUrl(asset.url());
        document.setThumbnailUrl(asset.thumbnailUrl());
        document.setAltText(asset.altText());
        document.setDurationSeconds(asset.durationSeconds());
        document.setCreatedAt(asset.createdAt() != null ? asset.createdAt() : now);
        document.setUpdatedAt(asset.updatedAt() != null ? asset.updatedAt() : now);
        return document;
    }

    private QuizReadModel.QuestionRead toQuestionRead(
            QuestionDocument question,
            List<AnswerOptionDocument> options,
            QuestionAssetDocument asset) {
        return new QuizReadModel.QuestionRead(
                question.getId(),
                question.getQuizId(),
                question.getTitle(),
                question.getDescription(),
                question.getType(),
                question.getDifficulty(),
                question.getExplanation(),
                question.getOrderIndex(),
                question.getTimeLimitSeconds(),
                question.getPoints(),
                options.stream()
                        .sorted(Comparator.comparingInt(AnswerOptionDocument::getOrderIndex))
                        .map(option -> new QuizReadModel.OptionRead(
                                option.getId(),
                                option.getQuestionId(),
                                option.getText(),
                                option.isCorrect(),
                                option.getExplanation(),
                                option.getOrderIndex(),
                                option.getCreatedAt(),
                                option.getUpdatedAt()))
                        .toList(),
                asset == null
                        ? null
                        : new QuizReadModel.AssetRead(
                                asset.getId(),
                                asset.getQuestionId(),
                                asset.getType(),
                                asset.getUrl(),
                                asset.getThumbnailUrl(),
                                asset.getAltText(),
                                asset.getDurationSeconds(),
                                asset.getCreatedAt(),
                                asset.getUpdatedAt()),
                question.getCreatedAt(),
                question.getUpdatedAt());
    }
}
