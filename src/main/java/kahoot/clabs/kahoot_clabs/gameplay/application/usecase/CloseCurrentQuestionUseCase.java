package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuestionResultResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class CloseCurrentQuestionUseCase {
    private final GameSessionRepository repository;

    public CloseCurrentQuestionUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public QuestionResultResponse execute(UUID sessionId) {
        var session = repository.findById(sessionId).orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        session.closeCurrentQuestion();
        var result = session.currentQuestion().map(QuestionResultResponse::from).orElseThrow();
        repository.save(session);
        return result;
    }
}
