package kahoot.clabs.kahoot_clabs.quiz.domain.entity;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class AnswerOption extends BaseEntity {

    private UUID questionId;
    private String text;
    private boolean correct;
    private int orderIndex;
    private String explanation;

    private AnswerOption(String text, boolean correct) {
        super(null);
        validateText(text);
        this.text = text.trim();
        this.correct = correct;
    }

    public static AnswerOption create(String text, boolean correct) {
        return new AnswerOption(text, correct);
    }

    private void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new DomainException("Answer option text cannot be empty");
        }
        if (text.trim().length() > 500) {
            throw new DomainException("Answer option text cannot exceed 500 characters");
        }
    }

    void assignQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    void assignOrderIndex(int orderIndex) {
        if (orderIndex < 0) {
            throw new DomainException("Order index cannot be negative");
        }
        this.orderIndex = orderIndex;
    }

    void updateText(String newText) {
        validateText(newText);
        this.text = newText.trim();
    }

    void markAsCorrect(boolean correct) {
        this.correct = correct;
    }

    public UUID getQuestionId() {
        return questionId;
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

    public String getExplanation() {
        return explanation;
    }
}
