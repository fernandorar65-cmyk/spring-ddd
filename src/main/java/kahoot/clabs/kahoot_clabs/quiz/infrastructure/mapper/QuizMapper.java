package kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper;

import java.math.BigDecimal;
import java.util.List;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.QuizCategory;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.EstimatedTime;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizSettings;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizStatus;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizVisibility;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.QuizCategoryEntity;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.QuizEntity;

public final class QuizMapper {

    private QuizMapper() {
    }

    public static QuizEntity toEntity(Quiz quiz) {
        QuizEntity entity = new QuizEntity();
        entity.setId(quiz.getId());
        entity.setOrganizationId(quiz.getOrganizationId());
        entity.setCreatedBy(quiz.getCreatedById());
        entity.setTitle(quiz.getTitle().value());
        entity.setDescription(quiz.getDescription());
        entity.setThumbnailUrl(quiz.getThumbnail());
        entity.setVisibility(quiz.getVisibility().name());
        entity.setStatus(quiz.getStatus().name());
        entity.setDifficulty(quiz.getDifficulty().name());
        entity.setEstimatedTimeMinutes(quiz.getEstimatedTime() == null
                ? null
                : Math.toIntExact(quiz.getEstimatedTime().toMinutes()));
        entity.setPlayCount(quiz.getPlayCount());
        entity.setAverageRating(BigDecimal.valueOf(quiz.getAverageRating()));
        entity.setTemplate(quiz.isTemplate());
        applySettings(entity, quiz.getSettings());
        entity.setCreatedAt(quiz.getCreatedAt());
        entity.setUpdatedAt(quiz.getUpdatedAt());
        entity.setCategories(quiz.getCategories().stream()
                .map(QuizMapper::toEntity)
                .toList());
        entity.setQuestions(quiz.getQuestions().stream()
                .map(QuestionMapper::toEntity)
                .toList());
        return entity;
    }

    public static Quiz toDomain(QuizEntity entity) {
        List<QuizCategory> categories = entity.getCategories().stream()
                .map(QuizMapper::toDomain)
                .toList();
        List<Question> questions = entity.getQuestions().stream()
                .map(QuestionMapper::toDomain)
                .toList();
        return Quiz.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getCreatedBy(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getThumbnailUrl(),
                QuizVisibility.valueOf(entity.getVisibility()),
                QuizStatus.valueOf(entity.getStatus()),
                QuizDifficulty.valueOf(entity.getDifficulty()),
                entity.getEstimatedTimeMinutes() == null
                        ? null
                        : EstimatedTime.ofMinutes(entity.getEstimatedTimeMinutes()),
                QuizSettings.of(
                        entity.isRandomQuestions(),
                        entity.isRandomAnswers(),
                        entity.isShowCorrectAnswer(),
                        entity.isShowRanking(),
                        entity.isAllowRetry(),
                        entity.isShowTimer(),
                        entity.isMusicEnabled()),
                entity.getPlayCount(),
                entity.getAverageRating().doubleValue(),
                entity.isTemplate(),
                categories,
                questions,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static QuizCategoryEntity toEntity(QuizCategory category) {
        QuizCategoryEntity entity = new QuizCategoryEntity();
        entity.setQuizId(category.getQuizId());
        entity.setCategoryId(category.getCategoryId());
        return entity;
    }

    private static QuizCategory toDomain(QuizCategoryEntity entity) {
        return QuizCategory.of(entity.getQuizId(), entity.getCategoryId());
    }

    private static void applySettings(QuizEntity entity, QuizSettings settings) {
        entity.setRandomQuestions(settings.isRandomQuestions());
        entity.setRandomAnswers(settings.isRandomAnswers());
        entity.setShowCorrectAnswer(settings.isShowCorrectAnswer());
        entity.setShowRanking(settings.isShowRanking());
        entity.setAllowRetry(settings.isAllowRetry());
        entity.setShowTimer(settings.isShowTimer());
        entity.setMusicEnabled(settings.isMusicEnabled());
    }
}
