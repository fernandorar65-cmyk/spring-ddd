package kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.QuizCategoryDocument;

public interface SpringQuizCategoryMongoRepository extends MongoRepository<QuizCategoryDocument, String> {

    List<QuizCategoryDocument> findByQuizId(UUID quizId);

    void deleteByQuizId(UUID quizId);
}
