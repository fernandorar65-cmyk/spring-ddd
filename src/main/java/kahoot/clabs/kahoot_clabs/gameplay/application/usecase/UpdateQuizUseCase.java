package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.QuizSettingsCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.UpdateQuizCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.EstimatedTime;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.QuizSettings;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class UpdateQuizUseCase {

    private final QuizRepository quizRepository;

    public UpdateQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse execute(UUID organizationId, UUID quizId, UpdateQuizCommand command) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to the organization");
        }

        quiz.rename(command.title());
        quiz.changeDescription(command.description());
        quiz.changeDifficulty(command.difficulty());
        quiz.changeEstimatedTime(EstimatedTime.ofMinutes(command.estimatedTimeMinutes()));
        quiz.changeSettings(toDomain(command.settings()));

        return QuizResponse.from(quizRepository.save(quiz));
    }

    private QuizSettings toDomain(QuizSettingsCommand settings) {
        return QuizSettings.of(
                settings.randomQuestions(),
                settings.randomAnswers(),
                settings.showCorrectAnswer(),
                settings.showRanking(),
                settings.allowRetry(),
                settings.showTimer(),
                settings.musicEnabled());
    }
}
