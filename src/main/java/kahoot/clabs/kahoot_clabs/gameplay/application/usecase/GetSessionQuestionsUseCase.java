package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuestionResultResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionQuestionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetCurrentSessionQuestionQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetSessionQuestionResultQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.ListSessionQuestionsQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.QuestionRead;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetSessionQuestionsUseCase {

    private final GameSessionReadModelPort gameSessionReadModelPort;

    public GetSessionQuestionsUseCase(GameSessionReadModelPort gameSessionReadModelPort) {
        this.gameSessionReadModelPort = gameSessionReadModelPort;
    }

    public List<SessionQuestionResponse> list(ListSessionQuestionsQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        SessionStatus status = SessionStatus.valueOf(session.status());
        boolean reveal = query.asHost() || status == SessionStatus.FINISHED
                || status == SessionStatus.QUESTION_RESULT;
        return session.questions().stream()
                .map(question -> SessionQuestionResponse.from(
                        question, reveal && question.closedAt() != null))
                .toList();
    }

    public SessionQuestionResponse current(GetCurrentSessionQuestionQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        SessionStatus status = SessionStatus.valueOf(session.status());
        QuestionRead question = session.questions().stream()
                .filter(candidate -> candidate.orderIndex() == session.currentQuestionIndex())
                .findFirst()
                .orElseThrow(() -> new DomainException("No current question in session"));
        boolean reveal = status == SessionStatus.QUESTION_RESULT || status == SessionStatus.FINISHED;
        return SessionQuestionResponse.from(question, reveal);
    }

    public QuestionResultResponse result(GetSessionQuestionResultQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        SessionStatus status = SessionStatus.valueOf(session.status());
        QuestionRead question = session.questions().stream()
                .filter(candidate -> candidate.id().equals(query.sessionQuestionId()))
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        "Session question not found: " + query.sessionQuestionId()));
        boolean closed = question.closedAt() != null;
        if (!closed
                && status != SessionStatus.FINISHED
                && status != SessionStatus.QUESTION_RESULT) {
            throw new DomainException("Question results are not available yet");
        }
        if (status == SessionStatus.QUESTION_OPEN
                && question.orderIndex() == session.currentQuestionIndex()) {
            throw new DomainException("Question results are not available while the question is open");
        }
        return QuestionResultResponse.from(session, question);
    }

    private GameSessionReadModel requireSession(UUID organizationId, UUID sessionId) {
        return gameSessionReadModelPort.findById(sessionId)
                .map(session -> {
                    if (!session.organizationId().equals(organizationId)) {
                        throw new DomainException(
                                "Game session does not belong to organization: " + organizationId);
                    }
                    return session;
                })
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }
}
