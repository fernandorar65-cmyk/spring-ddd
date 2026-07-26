package kahoot.clabs.kahoot_clabs.gameplay.domain.entity;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public final class SessionAnswerOption extends BaseEntity {

    private final UUID sessionQuestionId;
    private final UUID originalAnswerOptionId;
    private final String text;
    private final boolean correct;
    private final int orderIndex;

    private SessionAnswerOption(
            UUID id,
            UUID sessionQuestionId,
            UUID originalAnswerOptionId,
            String text,
            boolean correct,
            int orderIndex) {
        super(id);
        if (sessionQuestionId == null || originalAnswerOptionId == null) {
            throw new DomainException("Session question and original answer option ids are required");
        }
        if (text == null || text.isBlank() || orderIndex < 1) {
            throw new DomainException("Invalid session answer option snapshot");
        }
        this.sessionQuestionId = sessionQuestionId;
        this.originalAnswerOptionId = originalAnswerOptionId;
        this.text = text.trim();
        this.correct = correct;
        this.orderIndex = orderIndex;
    }

    public static SessionAnswerOption snapshot(UUID sessionQuestionId, AnswerOptionSnapshot snapshot) {
        return new SessionAnswerOption(
                null,
                sessionQuestionId,
                snapshot.originalOptionId(),
                snapshot.text(),
                snapshot.correct(),
                snapshot.orderIndex());
    }

    public static SessionAnswerOption rehydrate(
            UUID id,
            UUID sessionQuestionId,
            UUID originalAnswerOptionId,
            String text,
            boolean correct,
            int orderIndex) {
        return new SessionAnswerOption(id, sessionQuestionId, originalAnswerOptionId, text, correct, orderIndex);
    }

    public UUID getSessionQuestionId() {
        return sessionQuestionId;
    }

    public UUID getOriginalAnswerOptionId() {
        return originalAnswerOptionId;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getOrderIndex() {
        return orderIndex;
    }
}
