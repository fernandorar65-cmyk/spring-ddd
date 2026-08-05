package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;

public record PlayerAnswerResponse(
        UUID id,
        UUID sessionQuestionId,
        UUID sessionPlayerId,
        UUID sessionAnswerOptionId,
        boolean correct,
        long responseTimeMs,
        int awardedPoints,
        LocalDateTime answeredAt
) {

    public static PlayerAnswerResponse from(PlayerAnswer answer) {
        return new PlayerAnswerResponse(
                answer.getId(),
                answer.getSessionQuestionId(),
                answer.getSessionPlayerId(),
                answer.getSessionAnswerOptionId(),
                answer.isCorrect(),
                answer.getResponseTimeMs(),
                answer.getAwardedPoints(),
                answer.getAnsweredAt());
    }
}
