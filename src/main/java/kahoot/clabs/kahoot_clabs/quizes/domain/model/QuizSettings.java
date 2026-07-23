package kahoot.clabs.kahoot_clabs.quizes.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSettings {

    private boolean randomQuestions = false;      // Mezclar orden de preguntas
    private boolean randomAnswers = false;        // Mezclar orden de opciones
    private boolean showCorrectAnswer = true;     // Mostrar respuesta correcta al final
    private boolean showRanking = true;           // Mostrar leaderboard
    private boolean allowRetry = false;           // Permitir reintentar
    private boolean showTimer = true;             // Mostrar temporizador
    private boolean musicEnabled = false;         // Música de fondo

    // Constructor privado + método factory
    private QuizSettings() {}

    public static QuizSettings defaultSettings() {
        return new QuizSettings();
    }

    public static QuizSettings strictSettings() {
        QuizSettings settings = new QuizSettings();
        settings.setShowCorrectAnswer(false);
        settings.setAllowRetry(false);
        return settings;
    }
}