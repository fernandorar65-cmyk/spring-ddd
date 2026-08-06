package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;

public record QuestionResultResponse(
        UUID sessionQuestionId,
        int orderIndex,
        String title,
        UUID correctOptionId,
        int totalAnswers,
        int correctAnswers,
        Map<UUID, Long> optionCounts,
        List<PlayerAnswerResponse> answers
) {

    public static QuestionResultResponse from(GameSession session, SessionQuestion question) {
        List<PlayerAnswer> answers = session.answersForQuestion(question.getId());
        Map<UUID, Long> optionCounts = answers.stream()
                .filter(answer -> answer.getSessionAnswerOptionId() != null)
                .collect(Collectors.groupingBy(PlayerAnswer::getSessionAnswerOptionId, Collectors.counting()));
        UUID correctOptionId = question.findCorrectOption().map(option -> option.getId()).orElse(null);
        long correctCount = answers.stream().filter(PlayerAnswer::isCorrect).count();
        return new QuestionResultResponse(
                question.getId(),
                question.getOrderIndex(),
                question.getTitle(),
                correctOptionId,
                answers.size(),
                (int) correctCount,
                optionCounts,
                answers.stream().map(PlayerAnswerResponse::from).toList());
    }
}
