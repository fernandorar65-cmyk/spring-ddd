package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.LeaderboardEntryResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.query.GetLeaderboardQuery;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class GetLeaderboardUseCase {

    private final GameSessionRepository gameSessionRepository;

    public GetLeaderboardUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> execute(GetLeaderboardQuery query) {
        GameSession session = GameSessionSupport.requireSession(
                gameSessionRepository, query.organizationId(), query.sessionId());
        return LeaderboardEntryResponse.from(session);
    }
}
