package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.CreateGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class CreateGameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;

    public CreateGameSessionUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional
    public GameSessionResponse execute(CreateGameSessionCommand command) {
        GameSession session = GameSession.create(command.organizationId(), command.quizId(), command.hostUserId());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
