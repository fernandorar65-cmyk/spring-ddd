package kahoot.clabs.kahoot_clabs.gameplay.application.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.AnswerRead;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.OptionRead;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel.QuestionRead;

public record QuestionResultResponse(
                UUID sessionQuestionId,
                int orderIndex,
                String title,
                UUID correctOptionId,
                int totalAnswers,
                int correctAnswers,
                Map<UUID, Long> optionCounts,
                List<PlayerAnswerResponse> answers) {

        public static QuestionResultResponse from(GameSessionReadModel session, QuestionRead question) {
                List<AnswerRead> answers = session.answers().stream()
                                .filter(answer -> question.id().equals(answer.sessionQuestionId()))
                                .toList();
                Map<UUID, Long> optionCounts = answers.stream()
                                .filter(answer -> answer.sessionAnswerOptionId() != null)
                                .collect(Collectors.groupingBy(AnswerRead::sessionAnswerOptionId,
                                                Collectors.counting()));
                UUID correctOptionId = question.options().stream()
                                .filter(OptionRead::correct)
                                .map(OptionRead::id)
                                .findFirst()
                                .orElse(null);
                long correctCount = answers.stream().filter(AnswerRead::correct).count();
                return new QuestionResultResponse(
                                question.id(),
                                question.orderIndex(),
                                question.title(),
                                correctOptionId,
                                answers.size(),
                                (int) correctCount,
                                optionCounts,
                                answers.stream().map(PlayerAnswerResponse::from).toList());
        }
}
