package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class ListGameSessionsByQuizUseCase {

    private final GameSessionRepository gameSessionRepository;

    public ListGameSessionsByQuizUseCase(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Transactional(readOnly = true)
    public List<GameSessionResponse> execute(UUID quizId) {
        return gameSessionRepository.findByQuizId(quizId).stream()
                .map(GameSessionResponse::from)
                .toList();
    }
}
