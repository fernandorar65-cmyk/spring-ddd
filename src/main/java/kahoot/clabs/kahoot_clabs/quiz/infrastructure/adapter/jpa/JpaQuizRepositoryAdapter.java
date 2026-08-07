package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizProjectionPort;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModels;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper.QuizMapper;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.jpa.SpringQuizJpaRepository;

@Repository
public class JpaQuizRepositoryAdapter implements QuizRepository {

    private final SpringQuizJpaRepository springDataJpaRepository;
    private final ObjectProvider<QuizProjectionPort> quizProjectionPort;

    public JpaQuizRepositoryAdapter(
            SpringQuizJpaRepository springDataJpaRepository,
            ObjectProvider<QuizProjectionPort> quizProjectionPort) {
        this.springDataJpaRepository = springDataJpaRepository;
        this.quizProjectionPort = quizProjectionPort;
    }

    @Override
    public Quiz save(Quiz quiz) {
        Quiz saved = QuizMapper.toDomain(springDataJpaRepository.save(QuizMapper.toEntity(quiz)));
        quizProjectionPort.ifAvailable(port -> port.save(QuizReadModels.from(saved)));
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
        quizProjectionPort.ifAvailable(port -> port.deleteById(quiz.getId()));
    }

    @Override
    public void deleteById(UUID id) {
        springDataJpaRepository.deleteById(id);
        quizProjectionPort.ifAvailable(port -> port.deleteById(id));
    }
}
