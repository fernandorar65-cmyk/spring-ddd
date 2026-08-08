package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.QuizReadPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.ListQuizzesQuery;

@Service
public class ListQuizzesUseCase {

    private final QuizReadPort quizReadPort;

    public ListQuizzesUseCase(QuizReadPort quizReadPort) {
        this.quizReadPort = quizReadPort;
    }

    public List<QuizResponse> execute(ListQuizzesQuery query) {
        return quizReadPort.findByOrganizationIdOrderByUpdatedAtDesc(query.organizationId()).stream()
                .map(QuizResponse::from)
                .toList();
    }
}
