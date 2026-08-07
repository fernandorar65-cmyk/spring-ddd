package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.PlayerAnswerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetMyAnswersQuery;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.PlayerRead;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetMyAnswersUseCase {

    private final GameSessionReadModelPort gameSessionReadModelPort;

    public GetMyAnswersUseCase(GameSessionReadModelPort gameSessionReadModelPort) {
        this.gameSessionReadModelPort = gameSessionReadModelPort;
    }

    public List<PlayerAnswerResponse> execute(GetMyAnswersQuery query) {
        GameSessionReadModel session = requireSession(query.organizationId(), query.sessionId());
        PlayerRead player = session.players().stream()
                .filter(candidate -> candidate.userId().equals(query.userId()))
                .findFirst()
                .orElseThrow(() -> new DomainException("Player not found in session: " + query.userId()));
        return session.answers().stream()
                .filter(answer -> player.id().equals(answer.sessionPlayerId()))
                .map(PlayerAnswerResponse::from)
                .toList();
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
