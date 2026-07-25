package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.GameSessionEntity;

public interface SpringDataGameSessionRepository extends JpaRepository<GameSessionEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"players", "questions", "questions.answers"})
    Optional<GameSessionEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"players", "questions", "questions.answers"})
    Optional<GameSessionEntity> findByGamePin(String gamePin);

    @EntityGraph(attributePaths = {"players", "questions", "questions.answers"})
    List<GameSessionEntity> findByQuizId(UUID quizId);
}
