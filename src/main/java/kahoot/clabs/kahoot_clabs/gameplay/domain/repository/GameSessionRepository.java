package kahoot.clabs.kahoot_clabs.gameplay.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;

public interface GameSessionRepository {

    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID id);

    List<GameSession> findByOrganizationId(UUID organizationId);

    List<GameSession> findByOrganizationIdAndQuizId(UUID organizationId, UUID quizId);

    List<GameSession> findByOrganizationIdAndStatusIn(UUID organizationId, List<SessionStatus> statuses);
}
