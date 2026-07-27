package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.JoinGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class JoinGameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;

    public JoinGameSessionUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional
    public GameSessionResponse execute(UUID sessionId, JoinGameSessionCommand command) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        session.join(command.userId(), command.nickname());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
