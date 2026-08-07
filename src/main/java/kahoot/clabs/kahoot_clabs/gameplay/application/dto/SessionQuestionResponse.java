package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.OptionRead;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.QuestionRead;

public record SessionQuestionResponse(
        UUID id,
        int orderIndex,
        int points,
        int timeLimitSeconds,
        String title,
        String description,
        String questionType,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        List<OptionResponse> options
) {

    public static SessionQuestionResponse from(QuestionRead question, boolean revealCorrect) {
        return new SessionQuestionResponse(
                question.id(),
                question.orderIndex(),
                question.points(),
                question.timeLimitSeconds(),
                question.title(),
                question.description(),
                question.questionType(),
                question.openedAt(),
                question.closedAt(),
                question.options().stream()
                        .map(option -> OptionResponse.from(option, revealCorrect))
                        .toList());
    }

    public record OptionResponse(UUID id, String text, int orderIndex, Boolean correct) {

        private static OptionResponse from(OptionRead option, boolean revealCorrect) {
            return new OptionResponse(
                    option.id(),
                    option.text(),
                    option.orderIndex(),
                    revealCorrect ? option.correct() : null);
        }
    }
}
