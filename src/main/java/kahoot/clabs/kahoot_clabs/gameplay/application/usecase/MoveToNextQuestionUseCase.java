package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.CurrentQuestionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class MoveToNextQuestionUseCase {
    private final GameSessionRepository repository;

    public MoveToNextQuestionUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CurrentQuestionResponse execute(UUID sessionId) {
        var session = repository.findById(sessionId).orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        var next = session.nextQuestion().orElseThrow();
        repository.save(session);
        return CurrentQuestionResponse.from(next);
    }
}
