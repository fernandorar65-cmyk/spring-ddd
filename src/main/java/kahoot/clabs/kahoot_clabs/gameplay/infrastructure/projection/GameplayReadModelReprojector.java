package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.projection;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.CategoryProjectionPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.QuizProjectionPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.CategoryReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.QuizReadModels;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Manual reprojection helpers: rebuild Mongo read models from the confirmed PostgreSQL write model.
 * Not exposed as a public HTTP endpoint in the MVP.
 *
 * <pre>
 * gameplayReadModelReprojector.reprojectQuiz(quizId);
 * gameplayReadModelReprojector.reprojectGameSession(sessionId);
 * gameplayReadModelReprojector.reprojectCategory(categoryId);
 * </pre>
 */
@Component
public class GameplayReadModelReprojector {

    private static final Logger log = LoggerFactory.getLogger(GameplayReadModelReprojector.class);

    private final QuizRepository quizRepository;
    private final GameSessionRepository gameSessionRepository;
    private final CategoryRepository categoryRepository;
    private final QuizProjectionPort quizProjectionPort;
    private final GameSessionReadModelPort gameSessionReadModelPort;
    private final CategoryProjectionPort categoryProjectionPort;

    public GameplayReadModelReprojector(
            QuizRepository quizRepository,
            GameSessionRepository gameSessionRepository,
            CategoryRepository categoryRepository,
            QuizProjectionPort quizProjectionPort,
            GameSessionReadModelPort gameSessionReadModelPort,
            CategoryProjectionPort categoryProjectionPort) {
        this.quizRepository = quizRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.categoryRepository = categoryRepository;
        this.quizProjectionPort = quizProjectionPort;
        this.gameSessionReadModelPort = gameSessionReadModelPort;
        this.categoryProjectionPort = categoryProjectionPort;
    }

    @Transactional(readOnly = true)
    public void reprojectQuiz(UUID quizId) {
        var quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found for reprojection: " + quizId));
        log.info("reprojecting projectionType=quiz aggregateId={}", quizId);
        quizProjectionPort.save(QuizReadModels.from(quiz));
    }

    @Transactional(readOnly = true)
    public void reprojectGameSession(UUID sessionId) {
        var session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new DomainException("Game session not found for reprojection: " + sessionId));
        log.info("reprojecting projectionType=gameSession aggregateId={}", sessionId);
        gameSessionReadModelPort.save(GameSessionReadModels.from(session));
    }

    @Transactional(readOnly = true)
    public void reprojectCategory(UUID categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainException("Category not found for reprojection: " + categoryId));
        log.info("reprojecting projectionType=category aggregateId={}", categoryId);
        categoryProjectionPort.save(CategoryReadModels.from(category));
    }
}
