package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GamePin;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper.GameSessionMapper;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.SpringDataGameSessionRepository;

@Repository
public class JpaGameSessionRepositoryAdapter implements GameSessionRepository {

    private final SpringDataGameSessionRepository springDataRepository;

    public JpaGameSessionRepositoryAdapter(SpringDataGameSessionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public GameSession save(GameSession session) {
        return GameSessionMapper.toDomain(springDataRepository.save(GameSessionMapper.toEntity(session)));
    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return springDataRepository.findById(id).map(GameSessionMapper::toDomain);
    }

    @Override
    public Optional<GameSession> findByPin(GamePin pin) {
        return springDataRepository.findByGamePin(pin.value()).map(GameSessionMapper::toDomain);
    }

    @Override
    public List<GameSession> findByQuizId(UUID quizId) {
        return springDataRepository.findByQuizId(quizId).stream()
                .map(GameSessionMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(GameSession session) {
        springDataRepository.deleteById(session.getId());
    }
}
