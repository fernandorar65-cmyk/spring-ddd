package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.CurrentQuestionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetCurrentQuestionUseCase {
    private final GameSessionRepository repository;

    public GetCurrentQuestionUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CurrentQuestionResponse execute(UUID sessionId) {
        var session = repository.findById(sessionId).orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        return session.currentQuestion()
                .map(CurrentQuestionResponse::from)
                .orElseThrow(() -> new DomainException("There is no current question in this session"));
    }
}
