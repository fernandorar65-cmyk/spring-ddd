package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper.GameSessionMapper;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.GameSessionEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.PlayerAnswerEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa.GameSessionJpaRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa.PlayerAnswerJpaRepository;

/**
 * Test-profile read adapter: serves query use cases from Postgres/JPA without Mongo.
 */
@Repository
@Profile("test")
public class JpaGameSessionReadAdapter implements GameSessionReadModelPort {

    private final GameSessionJpaRepository sessionRepository;
    private final PlayerAnswerJpaRepository answerRepository;

    public JpaGameSessionReadAdapter(
            GameSessionJpaRepository sessionRepository,
            PlayerAnswerJpaRepository answerRepository) {
        this.sessionRepository = sessionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public Optional<GameSessionReadModel> findById(UUID id) {
        return sessionRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationId(UUID organizationId) {
        return sessionRepository.findByOrganizationId(organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status) {
        return findByOrganizationId(organizationId).stream()
                .filter(session -> session.status().equalsIgnoreCase(status))
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId) {
        return findByOrganizationId(organizationId).stream()
                .sorted(Comparator.comparing(
                        GameSessionReadModel::createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    public List<GameSessionReadModel> search(UUID organizationId, Collection<String> statuses, UUID quizId) {
        boolean hasStatuses = statuses != null && !statuses.isEmpty();
        List<GameSessionEntity> entities;
        if (quizId != null && hasStatuses) {
            Set<String> normalized = normalizeStatuses(statuses);
            entities = sessionRepository.findByOrganizationIdAndQuizId(organizationId, quizId).stream()
                    .filter(entity -> normalized.contains(entity.getStatus()))
                    .toList();
        } else if (quizId != null) {
            entities = sessionRepository.findByOrganizationIdAndQuizId(organizationId, quizId);
        } else if (hasStatuses) {
            entities = sessionRepository.findByOrganizationIdAndStatusIn(
                    organizationId, normalizeStatuses(statuses));
        } else {
            entities = sessionRepository.findByOrganizationId(organizationId);
        }
        return entities.stream()
                .map(this::toReadModel)
                .sorted(Comparator.comparing(
                        GameSessionReadModel::createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    public List<GameSessionReadModel> findByQuizId(UUID quizId) {
        return sessionRepository.findAll().stream()
                .map(this::toReadModel)
                .filter(session -> session.quizId().equals(quizId))
                .toList();
    }

    private static Set<String> normalizeStatuses(Collection<String> statuses) {
        return statuses.stream()
                .map(status -> status.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsByOrganizationIdAndId(UUID organizationId, UUID id) {
        return findById(id).filter(session -> session.organizationId().equals(organizationId)).isPresent();
    }

    @Override
    public void save(GameSessionReadModel readModel) {
        // no-op: Postgres remains source of truth in tests
    }

    @Override
    public void deleteById(UUID id) {
        // no-op
    }

    private GameSessionReadModel toReadModel(GameSessionEntity entity) {
        List<UUID> playerIds = entity.getPlayers().stream().map(player -> player.getId()).toList();
        List<PlayerAnswerEntity> answers = playerIds.isEmpty()
                ? List.of()
                : answerRepository.findBySessionPlayerIdIn(playerIds);
        GameSession session = GameSessionMapper.toDomain(entity, answers);
        return GameSessionReadModels.from(session);
    }
}
