package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;

public record CurrentQuestionResponse(
        UUID sessionQuestionId,
        int orderIndex,
        String title,
        String description,
        String type,
        int timeLimitSeconds,
        LocalDateTime startedAt,
        List<OptionResponse> options
) {
    public static CurrentQuestionResponse from(SessionQuestion question) {
        return new CurrentQuestionResponse(
                question.getId(),
                question.getOrderIndex(),
                question.getTitle(),
                question.getDescription(),
                question.getQuestionType(),
                question.getTimeLimitSeconds(),
                question.getOpenedAt(),
                question.getOptions().stream().map(OptionResponse::from).toList());
    }

    public record OptionResponse(UUID id, String text, int orderIndex) {
        private static OptionResponse from(SessionAnswerOption option) {
            return new OptionResponse(option.getId(), option.getText(), option.getOrderIndex());
        }
    }
}
