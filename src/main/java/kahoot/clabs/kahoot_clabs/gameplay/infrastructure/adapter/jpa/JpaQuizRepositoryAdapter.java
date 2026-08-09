package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.gameplay.application.event.QuizReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.event.QuizReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.QuizReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper.QuizMapper;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa.SpringQuizJpaRepository;

@Repository
public class JpaQuizRepositoryAdapter implements QuizRepository {

    private final SpringQuizJpaRepository springDataJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JpaQuizRepositoryAdapter(
            SpringQuizJpaRepository springDataJpaRepository,
            ApplicationEventPublisher eventPublisher) {
        this.springDataJpaRepository = springDataJpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Quiz save(Quiz quiz) {
        Quiz saved = QuizMapper.toDomain(springDataJpaRepository.save(QuizMapper.toEntity(quiz)));
        eventPublisher.publishEvent(new QuizReadModelUpsertedEvent(QuizReadModels.from(saved)));
        return saved;
    }

    @Override
    public Optional<Quiz> findById(UUID id) {
        return springDataJpaRepository.findById(id).map(QuizMapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title) {
        return springDataJpaRepository.existsByOrganizationIdAndTitleIgnoreCase(organizationId, title);
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataJpaRepository.existsById(id);
    }

    @Override
    public void delete(Quiz quiz) {
        springDataJpaRepository.delete(QuizMapper.toEntity(quiz));
        eventPublisher.publishEvent(new QuizReadModelDeletedEvent(quiz.getId()));
    }

    @Override
    public void deleteById(UUID id) {
        springDataJpaRepository.deleteById(id);
        eventPublisher.publishEvent(new QuizReadModelDeletedEvent(id));
    }
}
