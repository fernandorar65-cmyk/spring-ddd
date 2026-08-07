package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizReadModelPort;

@Service
public class ListQuizzesUseCase {

    private final QuizReadModelPort quizReadModelPort;

    public ListQuizzesUseCase(QuizReadModelPort quizReadModelPort) {
        this.quizReadModelPort = quizReadModelPort;
    }

    public List<QuizResponse> execute(UUID organizationId) {
        return quizReadModelPort.findByOrganizationIdOrderByUpdatedAtDesc(organizationId).stream()
                .map(QuizResponse::from)
                .toList();
    }
}
