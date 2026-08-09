package kahoot.clabs.kahoot_clabs.gameplay.application.port.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;


public interface GameSessionWriteModelPort {

    // GameSession
    GameSession save(GameSession session);

    Optional<GameSession> findGameSessionById(UUID id);

    List<GameSession> findByOrganizationId(UUID organizationId);

    List<GameSession> findByOrganizationIdAndQuizId(UUID organizationId, UUID quizId);

    List<GameSession> findByOrganizationIdAndStatusIn(UUID organizationId, List<SessionStatus> statuses);

    // Organization

    Organization save(Organization organization);

    Optional<Organization> findOrganizationById(UUID id);

    Optional<Organization> findOrganizationBySlug(String slug);

    boolean existsBySlug(String slug);

    void delete(Organization organization);

}
