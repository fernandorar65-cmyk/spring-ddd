package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper;

import java.util.List;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GamePin;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GameStatus;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.PlayerScore;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.ResponseTime;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.GameSessionEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.PlayerAnswerEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.SessionPlayerEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.SessionQuestionEntity;

public final class GameSessionMapper {

    private GameSessionMapper() {
    }

    public static GameSessionEntity toEntity(GameSession session) {
        GameSessionEntity entity = new GameSessionEntity();
        entity.setId(session.getId());
        entity.setOrganizationId(session.getOrganizationId());
        entity.setQuizId(session.getQuizId());
        entity.setHostUserId(session.getHostUserId());
        entity.setGamePin(session.getPin() == null ? null : session.getPin().value());
        entity.setStatus(session.getStatus().name());
        entity.setCurrentQuestionIndex(session.getCurrentQuestionIndex());
        entity.setStartedAt(session.getStartedAt());
        entity.setFinishedAt(session.getFinishedAt());
        entity.setCreatedAt(session.getCreatedAt());
        entity.setUpdatedAt(session.getUpdatedAt());
        entity.setPlayers(session.getPlayers().stream()
                .map(GameSessionMapper::toEntity)
                .toList());
        entity.setQuestions(session.getQuestions().stream()
                .map(GameSessionMapper::toEntity)
                .toList());
        return entity;
    }

    public static GameSession toDomain(GameSessionEntity entity) {
        List<SessionPlayer> players = entity.getPlayers().stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        List<SessionQuestion> questions = entity.getQuestions().stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        return GameSession.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getQuizId(),
                entity.getHostUserId(),
                entity.getGamePin() == null ? null : GamePin.of(entity.getGamePin()),
                GameStatus.valueOf(entity.getStatus()),
                entity.getCurrentQuestionIndex(),
                players,
                questions,
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static SessionPlayerEntity toEntity(SessionPlayer player) {
        SessionPlayerEntity entity = new SessionPlayerEntity();
        entity.setId(player.getId());
        entity.setSessionId(player.getGameSessionId());
        entity.setUserId(player.getUserId());
        entity.setNickname(player.getNickname());
        entity.setScore(player.getScore().value());
        entity.setConnected(player.isConnected());
        entity.setJoinedAt(player.getJoinedAt());
        entity.setLeftAt(player.getLeftAt());
        return entity;
    }

    private static SessionPlayer toDomain(SessionPlayerEntity entity) {
        return SessionPlayer.rehydrate(
                entity.getId(),
                entity.getSessionId(),
                entity.getUserId(),
                entity.getNickname(),
                PlayerScore.of(entity.getScore()),
                entity.isConnected(),
                entity.getJoinedAt(),
                entity.getLeftAt());
    }

    private static SessionQuestionEntity toEntity(SessionQuestion question) {
        SessionQuestionEntity entity = new SessionQuestionEntity();
        entity.setId(question.getId());
        entity.setSessionId(question.getGameSessionId());
        entity.setQuizQuestionId(question.getQuizQuestionId());
        entity.setOrderIndex(question.getOrderIndex());
        entity.setPoints(question.getPoints());
        entity.setTimeLimitSeconds(question.getTimeLimitSeconds());
        entity.setOpenedAt(question.getOpenedAt());
        entity.setClosedAt(question.getClosedAt());
        entity.setAnswers(question.getAnswers().stream()
                .map(GameSessionMapper::toEntity)
                .toList());
        return entity;
    }

    private static SessionQuestion toDomain(SessionQuestionEntity entity) {
        List<PlayerAnswer> answers = entity.getAnswers().stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        return SessionQuestion.rehydrate(
                entity.getId(),
                entity.getSessionId(),
                entity.getQuizQuestionId(),
                entity.getOrderIndex(),
                entity.getPoints(),
                entity.getTimeLimitSeconds(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                answers);
    }

    private static PlayerAnswerEntity toEntity(PlayerAnswer answer) {
        PlayerAnswerEntity entity = new PlayerAnswerEntity();
        entity.setId(answer.getId());
        entity.setSessionQuestionId(answer.getSessionQuestionId());
        entity.setSessionPlayerId(answer.getSessionPlayerId());
        entity.setAnswerOptionId(answer.getSelectedOptionId());
        entity.setCorrect(answer.isCorrect());
        entity.setResponseTimeMs(answer.getResponseTime().toMillis());
        entity.setAwardedPoints(answer.getAwardedPoints());
        entity.setAnsweredAt(answer.getAnsweredAt());
        return entity;
    }

    private static PlayerAnswer toDomain(PlayerAnswerEntity entity) {
        return PlayerAnswer.rehydrate(
                entity.getId(),
                entity.getSessionQuestionId(),
                entity.getSessionPlayerId(),
                entity.getAnswerOptionId(),
                entity.isCorrect(),
                ResponseTime.ofMillis(entity.getResponseTimeMs()),
                entity.getAwardedPoints(),
                entity.getAnsweredAt());
    }
}
