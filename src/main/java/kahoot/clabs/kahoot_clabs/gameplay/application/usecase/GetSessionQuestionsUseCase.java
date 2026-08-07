package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuestionResultResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionQuestionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetCurrentSessionQuestionQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetSessionQuestionResultQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.ListSessionQuestionsQuery;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetSessionQuestionsUseCase {

    private final GameSessionRepository gameSessionRepository;

    public GetSessionQuestionsUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionQuestionResponse> list(ListSessionQuestionsQuery query) {
        GameSession session = GameSessionSupport.requireSession(
                gameSessionRepository, query.organizationId(), query.sessionId());
        boolean reveal = query.asHost() || session.getStatus() == SessionStatus.FINISHED
                || session.getStatus() == SessionStatus.QUESTION_RESULT;
        return session.getQuestions().stream()
                .map(question -> SessionQuestionResponse.from(question, reveal && question.isClosed()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionQuestionResponse current(GetCurrentSessionQuestionQuery query) {
        GameSession session = GameSessionSupport.requireSession(
                gameSessionRepository, query.organizationId(), query.sessionId());
        SessionQuestion question = session.findCurrentQuestion()
                .orElseThrow(() -> new DomainException("No current question in session"));
        boolean reveal = session.getStatus() == SessionStatus.QUESTION_RESULT
                || session.getStatus() == SessionStatus.FINISHED;
        return SessionQuestionResponse.from(question, reveal);
    }

    @Transactional(readOnly = true)
    public QuestionResultResponse result(GetSessionQuestionResultQuery query) {
        GameSession session = GameSessionSupport.requireSession(
                gameSessionRepository, query.organizationId(), query.sessionId());
        SessionQuestion question = session.findQuestionById(query.sessionQuestionId())
                .orElseThrow(() -> new DomainException(
                        "Session question not found: " + query.sessionQuestionId()));
        if (!question.isClosed()
                && session.getStatus() != SessionStatus.FINISHED
                && session.getStatus() != SessionStatus.QUESTION_RESULT) {
            throw new DomainException("Question results are not available yet");
        }
        if (session.getStatus() == SessionStatus.QUESTION_OPEN
                && question.getOrderIndex() == session.getCurrentQuestionIndex()) {
            throw new DomainException("Question results are not available while the question is open");
        }
        return QuestionResultResponse.from(session, question);
    }
}
