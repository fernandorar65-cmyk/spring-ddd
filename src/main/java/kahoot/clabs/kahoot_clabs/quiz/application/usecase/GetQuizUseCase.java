package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetQuizUseCase {

    private final QuizRepository quizRepository;

    public GetQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public QuizResponse execute(UUID organizationId, UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to organization: " + organizationId);
        }
        return QuizResponse.from(quiz);
    }
}
