package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.jpa;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizProjectionPort;
import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizReadPort;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModel;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModels;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper.QuizMapper;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.jpa.SpringQuizJpaRepository;

/**
 * Test-profile read/projection adapter: serves queries from Postgres/JPA without Mongo.
 */
@Repository
@Profile("test")
public class JpaQuizReadAdapter implements QuizReadPort, QuizProjectionPort {

    private final SpringQuizJpaRepository springDataRepository;

    public JpaQuizReadAdapter(SpringQuizJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<QuizReadModel> findById(UUID id) {
        return springDataRepository.findById(id).map(QuizMapper::toDomain).map(QuizReadModels::from);
    }

    @Override
    public List<QuizReadModel> findByOrganizationId(UUID organizationId) {
        return springDataRepository.findByOrganizationId(organizationId).stream()
                .map(QuizMapper::toDomain)
                .map(QuizReadModels::from)
                .toList();
    }

    @Override
    public List<QuizReadModel> findByOrganizationIdAndStatus(UUID organizationId, String status) {
        return findByOrganizationId(organizationId).stream()
                .filter(quiz -> quiz.status().equalsIgnoreCase(status))
                .toList();
    }

    @Override
    public List<QuizReadModel> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId) {
        return findByOrganizationId(organizationId).stream()
                .sorted(Comparator.comparing(QuizReadModel::updatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndId(UUID organizationId, UUID id) {
        return findById(id).filter(quiz -> quiz.organizationId().equals(organizationId)).isPresent();
    }

    @Override
    public void save(QuizReadModel readModel) {
        // no-op: Postgres remains source of truth in tests
    }

    @Override
    public void deleteById(UUID id) {
        // no-op
    }
}
