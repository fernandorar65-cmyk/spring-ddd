package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;

public record AnswerSubmissionResponse(
        UUID answerId,
        UUID sessionQuestionId,
        UUID sessionPlayerId,
        boolean correct,
        int awardedPoints
) {
    public static AnswerSubmissionResponse from(PlayerAnswer answer) {
        return new AnswerSubmissionResponse(
                answer.getId(),
                answer.getSessionQuestionId(),
                answer.getSessionPlayerId(),
                answer.isCorrect(),
                answer.getAwardedPoints());
    }
}
