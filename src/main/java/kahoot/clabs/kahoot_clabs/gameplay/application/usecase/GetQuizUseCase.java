package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.QuizReadPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetQuizQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.QuizReadModel;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetQuizUseCase {

    private final QuizReadPort quizReadPort;

    public GetQuizUseCase(QuizReadPort quizReadPort) {
        this.quizReadPort = quizReadPort;
    }

    public QuizResponse execute(GetQuizQuery query) {
        QuizReadModel quiz = quizReadPort.findById(query.quizId())
                .orElseThrow(() -> new DomainException("Quiz not found: " + query.quizId()));
        if (!quiz.organizationId().equals(query.organizationId())) {
            throw new DomainException("Quiz does not belong to organization: " + query.organizationId());
        }
        return QuizResponse.fromDetails(quiz);
    }
}
