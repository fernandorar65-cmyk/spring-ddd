package kahoot.clabs.kahoot_clabs.gameplay.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GamePin;

public interface GameSessionRepository {

    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID id);

    List<GameSession> findAll();

    Optional<GameSession> findByPin(GamePin pin);

    List<GameSession> findByQuizId(UUID quizId);

    void delete(GameSession session);

    void deleteById(UUID id);
}
