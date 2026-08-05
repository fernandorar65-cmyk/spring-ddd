package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper.GameSessionMapper;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.GameSessionEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.PlayerAnswerEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.SpringDataGameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.SpringDataPlayerAnswerRepository;

@Repository
public class JpaGameSessionRepositoryAdapter implements GameSessionRepository {

    private final SpringDataGameSessionRepository sessionRepository;
    private final SpringDataPlayerAnswerRepository answerRepository;

    public JpaGameSessionRepositoryAdapter(
            SpringDataGameSessionRepository sessionRepository,
            SpringDataPlayerAnswerRepository answerRepository) {
        this.sessionRepository = sessionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public GameSession save(GameSession session) {
        GameSessionEntity saved = sessionRepository.save(GameSessionMapper.toEntity(session));
        syncAnswers(session);
        List<PlayerAnswerEntity> answers = loadAnswers(saved);
        return GameSessionMapper.toDomain(saved, answers);
    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return sessionRepository.findById(id).map(this::toAggregate);
    }

    @Override
    public List<GameSession> findByOrganizationId(UUID organizationId) {
        return sessionRepository.findByOrganizationId(organizationId).stream()
                .map(this::toAggregate)
                .toList();
    }

    @Override
    public List<GameSession> findByOrganizationIdAndQuizId(UUID organizationId, UUID quizId) {
        return sessionRepository.findByOrganizationIdAndQuizId(organizationId, quizId).stream()
                .map(this::toAggregate)
                .toList();
    }

    @Override
    public List<GameSession> findByOrganizationIdAndStatusIn(UUID organizationId, List<SessionStatus> statuses) {
        List<String> statusNames = statuses.stream().map(Enum::name).toList();
        return sessionRepository.findByOrganizationIdAndStatusIn(organizationId, statusNames).stream()
                .map(this::toAggregate)
                .toList();
    }

    private GameSession toAggregate(GameSessionEntity entity) {
        return GameSessionMapper.toDomain(entity, loadAnswers(entity));
    }

    private List<PlayerAnswerEntity> loadAnswers(GameSessionEntity entity) {
        List<UUID> playerIds = entity.getPlayers().stream()
                .map(player -> player.getId())
                .toList();
        if (playerIds.isEmpty()) {
            return List.of();
        }
        return answerRepository.findBySessionPlayerIdIn(playerIds);
    }

    private void syncAnswers(GameSession session) {
        Collection<UUID> playerIds = session.getPlayers().stream()
                .map(player -> player.getId())
                .toList();
        if (playerIds.isEmpty()) {
            return;
        }
        List<PlayerAnswerEntity> answerEntities = GameSessionMapper.toAnswerEntities(session);
        if (answerEntities.isEmpty()) {
            answerRepository.deleteBySessionPlayerIdIn(playerIds);
            return;
        }
        List<UUID> answerIds = answerEntities.stream().map(PlayerAnswerEntity::getId).toList();
        answerRepository.deleteBySessionPlayerIdInAndIdNotIn(playerIds, answerIds);
        answerRepository.saveAll(answerEntities);
    }
}
