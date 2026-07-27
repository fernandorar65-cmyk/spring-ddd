package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.JoinGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GamePin;

@Service
public class JoinGameSessionByPinUseCase {

    private final GameSessionRepository gameSessionRepository;

    public JoinGameSessionByPinUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional
    public GameSessionResponse execute(String pin, JoinGameSessionCommand command) {
        GameSession session = gameSessionRepository.findByPin(GamePin.of(pin))
                .orElseThrow(() -> new GameSessionNotFoundException(pin));
        session.join(command.userId(), command.nickname());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
