package kahoot.clabs.kahoot_clabs.quiz.application.readmodel;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.QuestionAsset;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizSettings;

public final class QuizReadModels {

    private QuizReadModels() {
    }

    public static QuizReadModel from(Quiz quiz) {
        QuizSettings settings = quiz.getSettings();
        return new QuizReadModel(
                quiz.getId(),
                quiz.getOrganizationId(),
                quiz.getCreatedById(),
                quiz.getTitle().value(),
                quiz.getDescription(),
                quiz.getThumbnail(),
                quiz.getStatus().name(),
                quiz.getDifficulty().name(),
                quiz.getEstimatedTime() == null ? null : quiz.getEstimatedTime().toMinutes(),
                quiz.getPlayCount(),
                quiz.getAverageRating(),
                quiz.isTemplate(),
                settings.isRandomQuestions(),
                settings.isRandomAnswers(),
                settings.isShowCorrectAnswer(),
                settings.isShowRanking(),
                settings.isAllowRetry(),
                settings.isShowTimer(),
                settings.isMusicEnabled(),
                quiz.getCategories().stream().map(category -> category.getCategoryId()).toList(),
                quiz.getQuestions().size(),
                quiz.getQuestions().stream().map(QuizReadModels::toQuestion).toList(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }

    private static QuizReadModel.QuestionRead toQuestion(Question question) {
        return new QuizReadModel.QuestionRead(
                question.getId(),
                question.getQuizId(),
                question.getTitle(),
                question.getDescription(),
                question.getType().name(),
                question.getDifficulty().name(),
                question.getExplanation(),
                question.getOrderIndex(),
                question.getTimeLimit().seconds(),
                question.getPoints().value(),
                question.getOptions().stream().map(QuizReadModels::toOption).toList(),
                toAsset(question.getAsset()),
                null,
                null);
    }

    private static QuizReadModel.OptionRead toOption(AnswerOption option) {
        return new QuizReadModel.OptionRead(
                option.getId(),
                option.getQuestionId(),
                option.getText(),
                option.isCorrect(),
                option.getExplanation(),
                option.getOrderIndex(),
                null,
                null);
    }

    private static QuizReadModel.AssetRead toAsset(QuestionAsset asset) {
        if (asset == null) {
            return null;
        }
        return new QuizReadModel.AssetRead(
                asset.getId(),
                asset.getQuestionId(),
                asset.getType().name(),
                asset.getUrl().value(),
                asset.getThumbnailUrl() == null ? null : asset.getThumbnailUrl().value(),
                asset.getAltText(),
                asset.getDurationSeconds(),
                null,
                null);
    }
}