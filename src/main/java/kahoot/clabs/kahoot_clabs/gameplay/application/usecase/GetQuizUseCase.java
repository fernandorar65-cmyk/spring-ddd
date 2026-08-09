package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetQuizQuery;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetQuizUseCase {

    private final QuizRepository quizRepository;

    public GetQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public QuizResponse execute(GetQuizQuery query) {
        Quiz quiz = quizRepository.findById(query.quizId())
                .orElseThrow(() -> new DomainException("Quiz not found: " + query.quizId()));
        if (!quiz.getOrganizationId().equals(query.organizationId())) {
            throw new DomainException("Quiz does not belong to organization: " + query.organizationId());
        }
        return QuizResponse.from(quiz);
    }
}
