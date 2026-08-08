package kahoot.clabs.kahoot_clabs.gameplay.application.command;

public record QuizSettingsCommand(
        boolean randomQuestions,
        boolean randomAnswers,
        boolean showCorrectAnswer,
        boolean showRanking,
        boolean allowRetry,
        boolean showTimer,
        boolean musicEnabled) {
}
