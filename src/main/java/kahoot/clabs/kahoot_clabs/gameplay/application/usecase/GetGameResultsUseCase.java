package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameResultsResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class GetGameResultsUseCase {
    private final GameSessionRepository repository;

    public GetGameResultsUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public GameResultsResponse execute(UUID sessionId) {
        return repository.findById(sessionId)
                .map(GameResultsResponse::from)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }
}
