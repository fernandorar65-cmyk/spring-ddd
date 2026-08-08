package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter.jpa;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.QuizSnapshotPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.AnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.QuizStatus;

@Component
public class JpaQuizSnapshotAdapter implements QuizSnapshotPort {

    private final QuizRepository quizRepository;

    public JpaQuizSnapshotAdapter(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public Optional<PublishedQuizSnapshot> findPublishedByOrganizationAndId(UUID organizationId, UUID quizId) {
        return quizRepository.findById(quizId)
                .filter(quiz -> quiz.getOrganizationId().equals(organizationId))
                .filter(quiz -> quiz.getStatus() == QuizStatus.PUBLISHED)
                .map(JpaQuizSnapshotAdapter::toSnapshot);
    }

    private static PublishedQuizSnapshot toSnapshot(Quiz quiz) {
        List<QuestionSnapshot> questions = quiz.getQuestions().stream()
                .sorted(Comparator.comparingInt(Question::getOrderIndex))
                .map(JpaQuizSnapshotAdapter::toQuestionSnapshot)
                .toList();
        return new PublishedQuizSnapshot(quiz.getId(), quiz.getOrganizationId(), questions);
    }

    private static QuestionSnapshot toQuestionSnapshot(Question question) {
        List<AnswerOptionSnapshot> options = question.getOptions().stream()
                .sorted(Comparator.comparingInt(AnswerOption::getOrderIndex))
                .map(option -> new AnswerOptionSnapshot(
                        option.getId(),
                        option.getText(),
                        option.isCorrect(),
                        option.getOrderIndex()))
                .toList();
        return new QuestionSnapshot(
                question.getId(),
                question.getOrderIndex(),
                question.getPoints().value(),
                question.getTimeLimit().seconds(),
                question.getTitle(),
                question.getDescription(),
                question.getType().name(),
                options);
    }
}
