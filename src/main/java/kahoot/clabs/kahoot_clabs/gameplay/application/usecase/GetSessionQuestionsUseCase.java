package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuestionResultResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionQuestionResponse;
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
    public List<SessionQuestionResponse> list(UUID organizationId, UUID sessionId, boolean asHost) {
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        boolean reveal = asHost || session.getStatus() == SessionStatus.FINISHED
                || session.getStatus() == SessionStatus.QUESTION_RESULT;
        return session.getQuestions().stream()
                .map(question -> SessionQuestionResponse.from(question, reveal && question.isClosed()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionQuestionResponse current(UUID organizationId, UUID sessionId) {
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        SessionQuestion question = session.findCurrentQuestion()
                .orElseThrow(() -> new DomainException("No current question in session"));
        boolean reveal = session.getStatus() == SessionStatus.QUESTION_RESULT
                || session.getStatus() == SessionStatus.FINISHED;
        return SessionQuestionResponse.from(question, reveal);
    }

    @Transactional(readOnly = true)
    public QuestionResultResponse result(UUID organizationId, UUID sessionId, UUID sessionQuestionId) {
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        SessionQuestion question = session.findQuestionById(sessionQuestionId)
                .orElseThrow(() -> new DomainException("Session question not found: " + sessionQuestionId));
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
