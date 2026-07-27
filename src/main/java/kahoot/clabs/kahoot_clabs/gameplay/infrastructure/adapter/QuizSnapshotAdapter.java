package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.QuizSnapshotPort;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizStatus;

@Component
public class QuizSnapshotAdapter implements QuizSnapshotPort {

    private final QuizRepository quizRepository;

    public QuizSnapshotAdapter(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public Optional<PublishedQuizSnapshot> findPublishedById(UUID quizId) {
        return quizRepository.findById(quizId)
                .filter(quiz -> quiz.getStatus() == QuizStatus.PUBLISHED)
                .map(quiz -> new PublishedQuizSnapshot(
                        quiz.getOrganizationId(),
                        quiz.getQuestions().stream()
                                .map(question -> new QuestionSnapshot(
                                        question.getId(),
                                        question.getTitle(),
                                        question.getDescription(),
                                        question.getType().name(),
                                        question.getPoints().value(),
                                        question.getTimeLimit().seconds(),
                                        question.getOptions().stream()
                                                .map(option -> new AnswerOptionSnapshot(
                                                        option.getId(),
                                                        option.getText(),
                                                        option.isCorrect(),
                                                        option.getOrderIndex()))
                                                .toList()))
                                .toList()));
    }
}
