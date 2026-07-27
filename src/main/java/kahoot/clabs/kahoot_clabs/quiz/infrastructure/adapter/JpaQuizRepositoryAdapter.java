package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.mapper.QuizMapper;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.SpringDataQuizRepository;

@Repository
public class JpaQuizRepositoryAdapter implements QuizRepository {

    private final SpringDataQuizRepository springDataRepository;

    public JpaQuizRepositoryAdapter(SpringDataQuizRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Quiz save(Quiz quiz) {
        return QuizMapper.toDomain(springDataRepository.save(QuizMapper.toEntity(quiz)));
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
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id);
    }

    @Override
    public void delete(Quiz quiz) {
        springDataRepository.deleteById(quiz.getId());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
}
