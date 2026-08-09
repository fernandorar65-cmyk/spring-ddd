package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(GameplayReadModelProjectionListener.class);

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
        log.info(
                "projecting eventType={} aggregateId={} projectionType=quiz action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            quizProjectionPort.save(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=quiz action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onQuizDeleted(QuizReadModelDeletedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=quiz action=delete",
                event.getClass().getSimpleName(),
                event.quizId());
        try {
            quizProjectionPort.deleteById(event.quizId());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=quiz action=delete",
                    event.getClass().getSimpleName(),
                    event.quizId(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCategoryUpserted(CategoryReadModelUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=category action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            categoryProjectionPort.save(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=category action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCategoryDeleted(CategoryReadModelDeletedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=category action=delete",
                event.getClass().getSimpleName(),
                event.categoryId());
        try {
            categoryProjectionPort.deleteById(event.categoryId());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=category action=delete",
                    event.getClass().getSimpleName(),
                    event.categoryId(),
                    ex);
            throw ex;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onGameSessionUpserted(GameSessionReadModelUpsertedEvent event) {
        log.info(
                "projecting eventType={} aggregateId={} projectionType=gameSession action=upsert",
                event.getClass().getSimpleName(),
                event.readModel().id());
        try {
            gameSessionReadModelPort.save(event.readModel());
        } catch (RuntimeException ex) {
            log.error(
                    "projection failed eventType={} aggregateId={} projectionType=gameSession action=upsert",
                    event.getClass().getSimpleName(),
                    event.readModel().id(),
                    ex);
            throw ex;
        }
    }
}
