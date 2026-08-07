package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizReadModelPort;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModels;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper.QuizMapper;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.jpa.SpringDataQuizRepository;

@Repository
public class JpaQuizRepositoryAdapter implements QuizRepository {

    private final SpringDataQuizRepository springDataRepository;
    private final ObjectProvider<QuizReadModelPort> quizReadModelPort;

    public JpaQuizRepositoryAdapter(
            SpringDataQuizRepository springDataRepository,
            ObjectProvider<QuizReadModelPort> quizReadModelPort) {
        this.springDataRepository = springDataRepository;
        this.quizReadModelPort = quizReadModelPort;
    }

    @Override
    public Quiz save(Quiz quiz) {
        Quiz saved = QuizMapper.toDomain(springDataRepository.save(QuizMapper.toEntity(quiz)));
        quizReadModelPort.ifAvailable(port -> port.save(QuizReadModels.from(saved)));
        return saved;
    }

    @Override
    public Optional<Quiz> findById(UUID id) {
        return springDataRepository.findById(id).map(QuizMapper::toDomain);
    }

    @Override
    public List<Quiz> findAll() {
        return springDataRepository.findAll().stream()
                .map(QuizMapper::toDomain)
                .toList();
    }

    @Override
    public List<Quiz> findByOrganizationId(UUID organizationId) {
        return springDataRepository.findByOrganizationId(organizationId).stream()
                .map(QuizMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title) {
        return springDataRepository.existsByOrganizationIdAndTitleIgnoreCase(organizationId, title);
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id);
    }

    @Override
    public void delete(Quiz quiz) {
        springDataRepository.deleteById(quiz.getId());
        quizReadModelPort.ifAvailable(port -> port.deleteById(quiz.getId()));
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
        quizReadModelPort.ifAvailable(port -> port.deleteById(id));
    }
}
