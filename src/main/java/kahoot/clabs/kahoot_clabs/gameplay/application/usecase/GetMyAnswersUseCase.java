package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.PlayerAnswerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class GetMyAnswersUseCase {

    private final GameSessionRepository gameSessionRepository;

    public GetMyAnswersUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional(readOnly = true)
    public List<PlayerAnswerResponse> execute(UUID organizationId, UUID sessionId, UUID userId) {
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        SessionPlayer player = session.findPlayerByUserId(userId)
                .orElseThrow(() -> new DomainException("Player not found in session: " + userId));
        return session.answersForPlayer(player.getId()).stream()
                .map(PlayerAnswerResponse::from)
                .toList();
    }
}
