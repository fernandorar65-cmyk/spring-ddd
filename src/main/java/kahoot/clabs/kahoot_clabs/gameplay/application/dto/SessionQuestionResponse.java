package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;

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

    public static SessionQuestionResponse from(SessionQuestion question, boolean revealCorrect) {
        return new SessionQuestionResponse(
                question.getId(),
                question.getOrderIndex(),
                question.getPoints(),
                question.getTimeLimitSeconds(),
                question.getTitle(),
                question.getDescription(),
                question.getQuestionType(),
                question.getOpenedAt(),
                question.getClosedAt(),
                question.getOptions().stream()
                        .map(option -> OptionResponse.from(option, revealCorrect))
                        .toList());
    }

    public record OptionResponse(UUID id, String text, int orderIndex, Boolean correct) {

        private static OptionResponse from(SessionAnswerOption option, boolean revealCorrect) {
            return new OptionResponse(
                    option.getId(),
                    option.getText(),
                    option.getOrderIndex(),
                    revealCorrect ? option.isCorrect() : null);
        }
    }
}
