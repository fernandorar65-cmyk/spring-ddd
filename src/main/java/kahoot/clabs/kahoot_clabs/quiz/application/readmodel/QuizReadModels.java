package kahoot.clabs.kahoot_clabs.quiz.application.readmodel;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;

public final class QuizReadModels {

    private QuizReadModels() {
    }

    public static QuizReadModel from(Quiz quiz) {
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
                quiz.getCategories().stream().map(category -> category.getCategoryId()).toList(),
                quiz.getQuestions().size(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }
}
