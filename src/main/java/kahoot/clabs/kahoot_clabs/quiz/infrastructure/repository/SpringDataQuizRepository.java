package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.QuizEntity;

public interface SpringDataQuizRepository extends JpaRepository<QuizEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"categories", "questions", "questions.answerOptions", "questions.asset"})
    Optional<QuizEntity> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"categories", "questions", "questions.answerOptions", "questions.asset"})
    List<QuizEntity> findAll();

    @EntityGraph(attributePaths = {"categories", "questions", "questions.answerOptions", "questions.asset"})
    List<QuizEntity> findByOrganizationId(UUID organizationId);

    boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title);
}
