package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.mongo.QuestionDocument;

public interface SpringQuestionMongoRepository extends MongoRepository<QuestionDocument, UUID> {

    List<QuestionDocument> findByQuizId(UUID quizId);

    void deleteByQuizId(UUID quizId);
}
