package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.GameSessionEntity;

public interface GameSessionJpaRepository extends JpaRepository<GameSessionEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {
            "players",
            "questions",
            "questions.answerOptions"
    })
    Optional<GameSessionEntity> findById(UUID id);

    @EntityGraph(attributePaths = {
            "players",
            "questions",
            "questions.answerOptions"
    })
    List<GameSessionEntity> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {
            "players",
            "questions",
            "questions.answerOptions"
    })
    List<GameSessionEntity> findByOrganizationIdAndQuizId(UUID organizationId, UUID quizId);

    @EntityGraph(attributePaths = {
            "players",
            "questions",
            "questions.answerOptions"
    })
    List<GameSessionEntity> findByOrganizationIdAndStatusIn(UUID organizationId, Collection<String> statuses);
}
