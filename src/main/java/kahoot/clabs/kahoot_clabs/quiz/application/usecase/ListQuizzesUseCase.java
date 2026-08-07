package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizReadModelPort;
import kahoot.clabs.kahoot_clabs.quiz.application.query.ListQuizzesQuery;

@Service
public class ListQuizzesUseCase {

    private final QuizReadModelPort quizReadModelPort;

    public ListQuizzesUseCase(QuizReadModelPort quizReadModelPort) {
        this.quizReadModelPort = quizReadModelPort;
    }

    public List<QuizResponse> execute(ListQuizzesQuery query) {
        return quizReadModelPort.findByOrganizationIdOrderByUpdatedAtDesc(query.organizationId()).stream()
                .map(QuizResponse::from)
                .toList();
    }
}
