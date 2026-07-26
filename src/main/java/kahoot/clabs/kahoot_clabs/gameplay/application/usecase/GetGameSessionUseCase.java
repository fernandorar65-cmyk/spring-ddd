package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class GetGameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;

    public GetGameSessionUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional(readOnly = true)
    public GameSessionResponse execute(UUID id) {
        return gameSessionRepository.findById(id)
                .map(GameSessionResponse::from)
                .orElseThrow(() -> new GameSessionNotFoundException(id));
    }
}
