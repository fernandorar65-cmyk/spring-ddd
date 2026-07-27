package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class FinishGameSessionUseCase {
    private final GameSessionRepository repository;

    public FinishGameSessionUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GameSessionResponse execute(UUID sessionId) {
        var session = repository.findById(sessionId).orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        session.finish();
        return GameSessionResponse.from(repository.save(session));
    }
}
