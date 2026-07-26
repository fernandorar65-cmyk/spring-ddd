package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.CreateGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.QuizSnapshotProvider;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class CreateGameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final QuizSnapshotProvider quizSnapshotProvider;

    public CreateGameSessionUseCase(
            GameSessionRepository gameSessionRepository,
            QuizSnapshotProvider quizSnapshotProvider) {
        this.gameSessionRepository = gameSessionRepository;
        this.quizSnapshotProvider = quizSnapshotProvider;
    }

    @Transactional
    public GameSessionResponse execute(CreateGameSessionCommand command) {
        QuizSnapshotProvider.PublishedQuizSnapshot quiz = quizSnapshotProvider.findPublishedById(command.quizId())
                .orElseThrow(() -> new DomainException("Published quiz not found: " + command.quizId()));
        if (!quiz.organizationId().equals(command.organizationId())) {
            throw new DomainException("Quiz does not belong to the organization");
        }

        GameSession session = GameSession.create(command.organizationId(), command.quizId(), command.hostUserId());
        quiz.questions().forEach(question -> session.addQuestionSnapshot(
                question.id(),
                question.title(),
                question.description(),
                question.type(),
                question.points(),
                question.timeLimitSeconds(),
                question.options()));
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
