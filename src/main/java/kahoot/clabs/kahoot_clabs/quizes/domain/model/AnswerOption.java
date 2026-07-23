package kahoot.clabs.kahoot_clabs.quizes.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AnswerOption {

    private final UUID id;
    private UUID questionId;

    private String text;
    private boolean isCorrect;
    private int orderIndex;

    private String explanation;   // Explicación opcional por opción

    // Constructor privado
    private AnswerOption(String text, boolean isCorrect) {
        validateText(text);
        this.id = UUID.randomUUID();
        this.text = text.trim();
        this.isCorrect = isCorrect;
    }

    public static AnswerOption create(String text, boolean isCorrect) {
        return new AnswerOption(text, isCorrect);
    }

    // Validaciones
    private void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de la opción no puede estar vacío");
        }
        if (text.trim().length() > 500) {
            throw new IllegalArgumentException("El texto de la opción no puede superar los 500 caracteres");
        }
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public void setOrderIndex(int orderIndex) {
        if (orderIndex < 0) {
            throw new IllegalArgumentException("El orderIndex no puede ser negativo");
        }
        this.orderIndex = orderIndex;
    }

    public void updateText(String newText) {
        validateText(newText);
        this.text = newText.trim();
    }

    public void markAsCorrect(boolean correct) {
        this.isCorrect = correct;
    }
}