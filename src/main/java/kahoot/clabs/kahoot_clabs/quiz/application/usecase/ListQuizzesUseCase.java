package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;

@Service
public class ListQuizzesUseCase {

    private final QuizRepository quizRepository;

    public ListQuizzesUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public List<QuizResponse> execute(UUID organizationId) {
        return quizRepository.findByOrganizationId(organizationId).stream()
                .map(QuizResponse::from)
                .toList();
    }
}
