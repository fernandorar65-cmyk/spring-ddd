package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.jpa;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper.QuizMapper;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizReadDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.jpa.SpringQuizJpaRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringQuizMongoRepository;

@Repository
public class JpaQuizRepositoryAdapter implements QuizRepository {

    private final SpringQuizJpaRepository springDataJpaRepository;
    private final SpringQuizMongoRepository springQuizMongoRepository;

    public JpaQuizRepositoryAdapter(
            SpringQuizJpaRepository springDataJpaRepository,
            SpringQuizMongoRepository springQuizMongoRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
        this.springQuizMongoRepository = springQuizMongoRepository;
    }

    @Override
    public Quiz save(Quiz quiz) {
        // save in jpa
        Quiz saved = QuizMapper.toDomain(springDataJpaRepository.save(QuizMapper.toEntity(quiz)));
        // save in mongo
        QuizReadDocument quizReadDocument = QuizMapper.toReadDocument(QuizMapper.toEntity(saved));
        springQuizMongoRepository.save(quizReadDocument);
        return saved;
    }

    @Override
    public Optional<Quiz> findById(UUID id) {
        return springDataJpaRepository.findById(id).map(QuizMapper::toDomain);
    }

    @Override
    public List<Quiz> findAll() {
        return springDataJpaRepository.findAll().stream()
                .map(QuizMapper::toDomain)
                .toList();
    }

    @Override
    public List<Quiz> findByOrganizationId(UUID organizationId) {
        return springDataJpaRepository.findByOrganizationId(organizationId).stream()
                .map(QuizMapper::toDomain)
                .toList();
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
        // delete in jpa
        springDataJpaRepository.delete(QuizMapper.toEntity(quiz));
        // delete en mongo
        springQuizMongoRepository.deleteById(quiz.getId());
    }

    @Override
    public void deleteById(UUID id) {
        // delete in jpa
        springDataJpaRepository.deleteById(id);
        // delete in mongo
        springQuizMongoRepository.deleteById(id);
    }
}
