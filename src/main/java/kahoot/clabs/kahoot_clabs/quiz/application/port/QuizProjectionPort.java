package kahoot.clabs.kahoot_clabs.quiz.application.port;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.QuizReadModel;

/**
 * Port for synchronizing quiz read models after write-side changes.
 */
public interface QuizProjectionPort {

    void save(QuizReadModel readModel);

    void deleteById(UUID id);
}
