package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.GameSessionEntity;

public interface SpringDataGameSessionRepository extends JpaRepository<GameSessionEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"players", "questions", "questions.options", "questions.answers"})
    Optional<GameSessionEntity> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"players", "questions", "questions.options", "questions.answers"})
    List<GameSessionEntity> findAll();

    @EntityGraph(attributePaths = {"players", "questions", "questions.options", "questions.answers"})
    Optional<GameSessionEntity> findByGamePin(String gamePin);

    @EntityGraph(attributePaths = {"players", "questions", "questions.options", "questions.answers"})
    List<GameSessionEntity> findByQuizId(UUID quizId);
}
