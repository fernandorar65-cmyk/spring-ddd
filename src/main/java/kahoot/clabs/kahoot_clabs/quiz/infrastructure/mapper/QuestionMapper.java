package kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper;

import java.time.LocalDateTime;
import java.util.List;

import kahoot.clabs.kahoot_clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.QuestionAsset;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.MediaUrl;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.Points;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.TimeLimit;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.AnswerOptionEntity;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.QuestionAssetEntity;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.QuestionEntity;

public final class QuestionMapper {

    private QuestionMapper() {
    }

    public static QuestionEntity toEntity(Question question) {
        QuestionEntity entity = new QuestionEntity();
        entity.setId(question.getId());
        entity.setQuizId(question.getQuizId());
        entity.setTitle(question.getTitle());
        entity.setDescription(question.getDescription());
        entity.setType(question.getType().name());
        entity.setDifficulty(question.getDifficulty().name());
        entity.setExplanation(question.getExplanation());
        entity.setOrderIndex(question.getOrderIndex());
        entity.setTimeLimitSeconds(question.getTimeLimit().seconds());
        entity.setPoints(question.getPoints().value());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setAnswerOptions(question.getOptions().stream()
                .map(QuestionMapper::toEntity)
                .toList());
        if (question.getAsset() != null) {
            entity.setAsset(toEntity(question.getAsset()));
        }
        return entity;
    }

    public static Question toDomain(QuestionEntity entity) {
        List<AnswerOption> options = entity.getAnswerOptions().stream()
                .map(QuestionMapper::toDomain)
                .toList();
        QuestionAsset asset = entity.getAsset() == null ? null : toDomain(entity.getAsset());
        return Question.rehydrate(
                entity.getId(),
                entity.getQuizId(),
                entity.getTitle(),
                entity.getDescription(),
                QuestionType.valueOf(entity.getType()),
                Points.of(entity.getPoints()),
                TimeLimit.ofSeconds(entity.getTimeLimitSeconds()),
                entity.getOrderIndex(),
                entity.getExplanation(),
                QuizDifficulty.valueOf(entity.getDifficulty()),
                options,
                asset);
    }

    private static AnswerOptionEntity toEntity(AnswerOption option) {
        AnswerOptionEntity entity = new AnswerOptionEntity();
        entity.setId(option.getId());
        entity.setQuestionId(option.getQuestionId());
        entity.setText(option.getText());
        entity.setCorrect(option.isCorrect());
        entity.setExplanation(option.getExplanation());
        entity.setOrderIndex(option.getOrderIndex());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private static AnswerOption toDomain(AnswerOptionEntity entity) {
        return AnswerOption.rehydrate(
                entity.getId(),
                entity.getQuestionId(),
                entity.getText(),
                entity.isCorrect(),
                entity.getOrderIndex(),
                entity.getExplanation());
    }

    private static QuestionAssetEntity toEntity(QuestionAsset asset) {
        QuestionAssetEntity entity = new QuestionAssetEntity();
        entity.setId(asset.getId());
        entity.setQuestionId(asset.getQuestionId());
        entity.setType(asset.getType().name());
        entity.setUrl(asset.getUrl().value());
        entity.setThumbnailUrl(asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value());
        entity.setAltText(asset.getAltText());
        entity.setDurationSeconds(asset.getDurationSeconds());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private static QuestionAsset toDomain(QuestionAssetEntity entity) {
        return QuestionAsset.rehydrate(
                entity.getId(),
                entity.getQuestionId(),
                MediaType.valueOf(entity.getType()),
                MediaUrl.of(entity.getUrl()),
                entity.getThumbnailUrl() == null ? null : MediaUrl.of(entity.getThumbnailUrl()),
                entity.getAltText(),
                entity.getDurationSeconds());
    }
}
