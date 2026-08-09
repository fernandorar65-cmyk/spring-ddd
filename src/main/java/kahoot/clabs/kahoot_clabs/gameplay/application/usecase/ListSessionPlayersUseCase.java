package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionPlayerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.ListSessionPlayersQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class ListSessionPlayersUseCase {

    private final GameSessionReadModelPort gameSessionReadModelPort;

    public ListSessionPlayersUseCase(GameSessionReadModelPort gameSessionReadModelPort) {
        this.gameSessionReadModelPort = gameSessionReadModelPort;
    }

    public List<SessionPlayerResponse> execute(ListSessionPlayersQuery query) {
        GameSessionReadModel session = gameSessionReadModelPort.findById(query.sessionId())
                .orElseThrow(() -> new DomainException("Game session not found: " + query.sessionId()));
        if (!session.organizationId().equals(query.organizationId())) {
            throw new DomainException("Game session does not belong to organization: " + query.organizationId());
        }
        return session.players().stream().map(SessionPlayerResponse::from).toList();
    }
}
