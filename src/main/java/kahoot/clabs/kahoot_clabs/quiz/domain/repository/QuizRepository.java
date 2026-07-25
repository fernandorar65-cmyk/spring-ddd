package kahoot.clabs.kahoot_clabs.quiz.domain.repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;

public interface QuizRepository {

    Quiz save(Quiz quiz);

    Optional<Quiz> findById(UUID id);

    List<Quiz> findAll();

    boolean existsById(UUID id);

    void delete(Quiz quiz);

    void deleteById(UUID id);
}
