package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.LeaderboardResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class GetLeaderboardUseCase {
    private final GameSessionRepository repository;

    public GetLeaderboardUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LeaderboardResponse execute(UUID sessionId) {
        return repository.findById(sessionId)
                .map(LeaderboardResponse::from)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }
}
