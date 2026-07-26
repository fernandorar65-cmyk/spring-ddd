package kahoot.clabs.kahoot_clabs.quiz.domain.event;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainEvent;

/**
 * Reserved domain event: raised conceptually when a quiz is published.
 * Not published or consumed yet (no event bus / outbox / listeners).
 */
public class QuizPublishedEvent extends DomainEvent {

    private final UUID quizId;
    private final UUID organizationId;
    private final UUID publishedById;

    public QuizPublishedEvent(UUID quizId, UUID organizationId, UUID publishedById) {
        this.quizId = quizId;
        this.organizationId = organizationId;
        this.publishedById = publishedById;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getPublishedById() {
        return publishedById;
    }
}
