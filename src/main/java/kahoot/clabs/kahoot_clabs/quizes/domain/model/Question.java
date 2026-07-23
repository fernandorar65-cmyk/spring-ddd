package kahoot.clabs.kahoot_clabs.quizes.domain.model;

import kahoot.clabs.kahoot_clabs.quizes.domain.Enums.QuestionType;
import java.util.ArrayList;
import lombok.Getter;
import java.util.List;
import java.util.UUID;


@Getter
public class Question {

    private final UUID id;
    private UUID quizId;

    private String title;
    private String description;
    private QuestionType type;
    private int points;
    private int timeLimit;          // en segundos
    private int orderIndex;
    private String explanation;     // Explicación de la respuesta correcta

    private List<AnswerOption> options = new ArrayList<>();
    private QuestionMedia media;    // Imagen, video, etc. (opcional)

    private String difficulty;      // Ej: EASY, MEDIUM, HARD

    public Question(String title, QuestionType type) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.type = type;
        this.points = 1000;
        this.timeLimit = 30;        // valor por defecto
    }

    public void addAnswerOption(AnswerOption option) {
        option.setOrderIndex(options.size() + 1);
        options.add(option);
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}