package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetGameSessionQuery;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetGameSessionUseCase {

    private final GameSessionReadModelPort gameSessionReadModelPort;

    public GetGameSessionUseCase(GameSessionReadModelPort gameSessionReadModelPort) {
        this.gameSessionReadModelPort = gameSessionReadModelPort;
    }

    public GameSessionResponse execute(GetGameSessionQuery query) {
        return gameSessionReadModelPort.findById(query.sessionId())
                .map(session -> {
                    if (!session.organizationId().equals(query.organizationId())) {
                        throw new DomainException(
                                "Game session does not belong to organization: " + query.organizationId());
                    }
                    return GameSessionResponse.from(session);
                })
                .orElseThrow(() -> new GameSessionNotFoundException(query.sessionId()));
    }
}
