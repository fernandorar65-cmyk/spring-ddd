package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.SubmitAnswerCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.AnswerSubmissionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class SubmitAnswerUseCase {
    private final GameSessionRepository repository;

    public SubmitAnswerUseCase(GameSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AnswerSubmissionResponse execute(UUID sessionId, SubmitAnswerCommand command) {
        var session = repository.findById(sessionId).orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        var answer = session.submitAnswer(
                command.sessionPlayerId(), command.sessionQuestionId(), command.answerOptionId());
        repository.save(session);
        return AnswerSubmissionResponse.from(answer);
    }
}
