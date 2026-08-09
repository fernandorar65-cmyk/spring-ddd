package kahoot.clabs.kahoot_clabs.gameplay.application.readmodel;

import java.util.List;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;

public final class GameSessionReadModels {

        private GameSessionReadModels() {
        }

        public static GameSessionReadModel from(GameSession session) {
                List<GameSessionReadModel.PlayerRead> players = session.getPlayers().stream()
                                .map(GameSessionReadModels::toPlayer)
                                .toList();
                List<GameSessionReadModel.QuestionRead> questions = session.getQuestions().stream()
                                .map(GameSessionReadModels::toQuestion)
                                .toList();
                List<GameSessionReadModel.AnswerRead> answers = session.getAnswers().stream()
                                .map(GameSessionReadModels::toAnswer)
                                .toList();
                return new GameSessionReadModel(
                                session.getId(),
                                session.getOrganizationId(),
                                session.getQuizId(),
                                session.getHostUserId(),
                                session.getStatus().name(),
                                session.getCurrentQuestionIndex(),
                                players.size(),
                                questions.size(),
                                session.getStartedAt(),
                                session.getFinishedAt(),
                                session.getCreatedAt(),
                                session.getUpdatedAt(),
                                players,
                                questions,
                                answers);
        }

        private static GameSessionReadModel.PlayerRead toPlayer(SessionPlayer player) {
                return new GameSessionReadModel.PlayerRead(
                                player.getId(),
                                player.getUserId(),
                                player.getNickname().value(),
                                player.getScore(),
                                player.isConnected(),
                                player.getJoinedAt());
        }

        private static GameSessionReadModel.QuestionRead toQuestion(SessionQuestion question) {
                List<GameSessionReadModel.OptionRead> options = question.getOptions().stream()
                                .map(GameSessionReadModels::toOption)
                                .toList();
                return new GameSessionReadModel.QuestionRead(
                                question.getId(),
                                question.getOrderIndex(),
                                question.getPoints(),
                                question.getTimeLimitSeconds(),
                                question.getTitle(),
                                question.getDescription(),
                                question.getQuestionType(),
                                question.getOpenedAt(),
                                question.getClosedAt(),
                                options);
        }

        private static GameSessionReadModel.OptionRead toOption(SessionAnswerOption option) {
                return new GameSessionReadModel.OptionRead(
                                option.getId(),
                                option.getText(),
                                option.getOrderIndex(),
                                option.isCorrect());
        }

        private static GameSessionReadModel.AnswerRead toAnswer(PlayerAnswer answer) {
                return new GameSessionReadModel.AnswerRead(
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
