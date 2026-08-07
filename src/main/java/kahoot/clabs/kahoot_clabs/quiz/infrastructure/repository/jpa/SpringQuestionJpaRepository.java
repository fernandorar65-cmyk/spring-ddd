package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.QuestionEntity;

public interface SpringQuestionJpaRepository extends JpaRepository<QuestionEntity, UUID> {

    @EntityGraph(attributePaths = {"answerOptions", "asset"})
    List<QuestionEntity> findByQuizIdOrderByOrderIndex(UUID quizId);
}
