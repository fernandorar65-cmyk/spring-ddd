package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;

public record QuestionResultResponse(
        UUID sessionQuestionId,
        LocalDateTime closedAt,
        int answerCount,
        List<OptionResultResponse> options
) {
    public static QuestionResultResponse from(SessionQuestion question) {
        return new QuestionResultResponse(
                question.getId(),
                question.getClosedAt(),
                question.getAnswers().size(),
                question.getOptions().stream().map(OptionResultResponse::from).toList());
    }

    public record OptionResultResponse(UUID id, String text, int orderIndex, boolean correct) {
        private static OptionResultResponse from(SessionAnswerOption option) {
            return new OptionResultResponse(option.getId(), option.getText(), option.getOrderIndex(), option.isCorrect());
        }
    }
}
