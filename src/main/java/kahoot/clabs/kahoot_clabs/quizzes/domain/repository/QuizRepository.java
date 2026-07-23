package kahoot.clabs.kahoot_clabs.quizzes.domain.repository;

import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quizzes.domain.model.Quiz;

public interface QuizRepository {

    Quiz save(Quiz quiz);

    Optional<Quiz> findById(UUID id);

    void delete(Quiz quiz);
}
