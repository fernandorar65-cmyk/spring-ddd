package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kahoot.clabs.kahoot_clabs.gameplay.application.event.CategoryReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.event.CategoryReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.event.GameSessionReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.event.QuizReadModelDeletedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.event.QuizReadModelUpsertedEvent;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.CategoryProjectionPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.QuizProjectionPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.GameSessionReadModelPort;

@Component
public class GameplayReadModelProjectionListener {

    private final QuizProjectionPort quizProjectionPort;
    private final CategoryProjectionPort categoryProjectionPort;
    private final GameSessionReadModelPort gameSessionReadModelPort;

    public GameplayReadModelProjectionListener(
            QuizProjectionPort quizProjectionPort,
            CategoryProjectionPort categoryProjectionPort,
            GameSessionReadModelPort gameSessionReadModelPort) {
        this.quizProjectionPort = quizProjectionPort;
        this.categoryProjectionPort = categoryProjectionPort;
        this.gameSessionReadModelPort = gameSessionReadModelPort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onQuizUpserted(QuizReadModelUpsertedEvent event) {
        quizProjectionPort.save(event.readModel());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onQuizDeleted(QuizReadModelDeletedEvent event) {
        quizProjectionPort.deleteById(event.quizId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCategoryUpserted(CategoryReadModelUpsertedEvent event) {
        categoryProjectionPort.save(event.readModel());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCategoryDeleted(CategoryReadModelDeletedEvent event) {
        categoryProjectionPort.deleteById(event.categoryId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onGameSessionUpserted(GameSessionReadModelUpsertedEvent event) {
        gameSessionReadModelPort.save(event.readModel());
    }
}
