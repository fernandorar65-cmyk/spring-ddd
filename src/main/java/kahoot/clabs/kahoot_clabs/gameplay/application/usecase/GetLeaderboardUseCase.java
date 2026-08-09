package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.LeaderboardEntryResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetLeaderboardQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetLeaderboardUseCase {

    private final GameSessionReadModelPort gameSessionReadModelPort;

    public GetLeaderboardUseCase(GameSessionReadModelPort gameSessionReadModelPort) {
        this.gameSessionReadModelPort = gameSessionReadModelPort;
    }

    public List<LeaderboardEntryResponse> execute(GetLeaderboardQuery query) {
        return LeaderboardEntryResponse.from(requireSession(query.organizationId(), query.sessionId()));
    }

    private GameSessionReadModel requireSession(UUID organizationId, UUID sessionId) {
        return gameSessionReadModelPort.findById(sessionId)
                .map(session -> {
                    if (!session.organizationId().equals(organizationId)) {
                        throw new DomainException(
                                "Game session does not belong to organization: " + organizationId);
                    }
                    return session;
                })
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
    }
}
