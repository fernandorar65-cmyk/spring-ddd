package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter;

import java.util.Optional;
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
    public void delete(Quiz quiz) {
        springDataRepository.deleteById(quiz.getId());
    }
}
