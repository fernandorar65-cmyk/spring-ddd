package kahoot.clabs.kahoot_clabs.quiz.domain.repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;

public interface QuizRepository {

    // escritura x jpa
    
    Quiz save(Quiz quiz);

    boolean existsById(UUID id);
    
    void delete(Quiz quiz);
    
    void deleteById(UUID id);
    
    // lectura x mongo
    
    Optional<Quiz> findById(UUID id);
    
    List<Quiz> findAll();
    
    List<Quiz> findByOrganizationId(UUID organizationId);

    boolean existsByOrganizationIdAndTitleIgnoreCase(UUID organizationId, String title);
}
