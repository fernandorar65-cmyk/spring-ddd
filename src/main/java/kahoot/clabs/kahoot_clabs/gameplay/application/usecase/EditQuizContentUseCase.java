package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.AnswerOptionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.QuestionAssetCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.ReorderAnswerOptionsCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class EditQuizContentUseCase {

    private final QuizRepository quizRepository;

    public EditQuizContentUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse addAnswerOption(
            UUID organizationId, UUID quizId, UUID questionId, AnswerOptionCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.addAnswerOption(questionId, command.text(), command.correct());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse updateAnswerOption(
            UUID organizationId, UUID quizId, UUID questionId, UUID optionId, AnswerOptionCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.updateAnswerOption(questionId, optionId, command.text(), command.correct());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse reorderAnswerOptions(
            UUID organizationId, UUID quizId, UUID questionId, ReorderAnswerOptionsCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.reorderAnswerOptions(questionId, command.optionIds());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public void removeAnswerOption(UUID organizationId, UUID quizId, UUID questionId, UUID optionId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.removeAnswerOption(questionId, optionId);
        quizRepository.save(quiz);
    }

    @Transactional
    public QuizResponse addAsset(UUID organizationId, UUID quizId, UUID questionId, QuestionAssetCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.attachAsset(
                questionId,
                command.type(),
                command.url(),
                command.thumbnailUrl(),
                command.altText(),
                command.durationSeconds());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse updateAsset(
            UUID organizationId, UUID quizId, UUID questionId, UUID assetId, QuestionAssetCommand command) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.updateQuestionAsset(
                questionId,
                assetId,
                command.type(),
                command.url(),
                command.thumbnailUrl(),
                command.altText(),
                command.durationSeconds());
        return QuizResponse.from(quizRepository.save(quiz));
    }

    @Transactional
    public void removeAsset(UUID organizationId, UUID quizId, UUID questionId, UUID assetId) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        quiz.removeQuestionAsset(questionId, assetId);
        quizRepository.save(quiz);
    }

    private Quiz requireOwnedQuiz(UUID organizationId, UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to the organization");
        }
        return quiz;
    }
}
