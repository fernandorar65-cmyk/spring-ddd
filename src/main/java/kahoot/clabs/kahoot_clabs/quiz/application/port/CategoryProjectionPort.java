package kahoot.clabs.kahoot_clabs.quiz.application.port;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.CategoryReadModel;

public interface CategoryProjectionPort {

    void save(CategoryReadModel readModel);

    void deleteById(UUID id);
}
