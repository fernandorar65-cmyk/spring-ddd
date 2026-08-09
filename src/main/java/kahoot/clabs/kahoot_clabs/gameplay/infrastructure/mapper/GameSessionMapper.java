package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.GameSessionEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.PlayerAnswerEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.SessionAnswerOptionEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.SessionPlayerEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.SessionQuestionEntity;

public final class GameSessionMapper {

    private GameSessionMapper() {
    }

    public static GameSessionEntity toEntity(GameSession session) {
        GameSessionEntity entity = new GameSessionEntity();
        entity.setId(session.getId());
        entity.setOrganizationId(session.getOrganizationId());
        entity.setQuizId(session.getQuizId());
        entity.setHostUserId(session.getHostUserId());
        entity.setStatus(session.getStatus().name());
        entity.setCurrentQuestionIndex(session.getCurrentQuestionIndex());
        entity.setStartedAt(session.getStartedAt());
        entity.setFinishedAt(session.getFinishedAt());
        entity.setCreatedAt(session.getCreatedAt());
        entity.setUpdatedAt(session.getUpdatedAt());

        Set<SessionPlayerEntity> players = new HashSet<>();
        for (SessionPlayer player : session.getPlayers()) {
            SessionPlayerEntity playerEntity = toEntity(player);
            playerEntity.setSession(entity);
            players.add(playerEntity);
        }
        entity.setPlayers(new ArrayList<>(players));

        List<SessionQuestionEntity> questions = new ArrayList<>();
        for (SessionQuestion question : session.getQuestions()) {
            SessionQuestionEntity questionEntity = toEntity(question);
            questionEntity.setSession(entity);
            questions.add(questionEntity);
        }
        entity.setQuestions(questions);
        return entity;
    }

    public static GameSession toDomain(
            GameSessionEntity entity,
            List<PlayerAnswerEntity> answerEntities) {
        List<SessionPlayer> players = entity.getPlayers().stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        List<SessionQuestion> questions = entity.getQuestions().stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        List<PlayerAnswer> answers = answerEntities.stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        return GameSession.rehydrate(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getQuizId(),
                entity.getHostUserId(),
                SessionStatus.valueOf(entity.getStatus()),
                entity.getCurrentQuestionIndex(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                players,
                questions,
                answers,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static List<PlayerAnswerEntity> toAnswerEntities(GameSession session) {
        return session.getAnswers().stream().map(GameSessionMapper::toEntity).toList();
    }

    private static SessionPlayerEntity toEntity(SessionPlayer player) {
        SessionPlayerEntity entity = new SessionPlayerEntity();
        entity.setId(player.getId());
        entity.setSessionId(player.getSessionId());
        entity.setUserId(player.getUserId());
        entity.setNickname(player.getNickname().value());
        entity.setScore(player.getScore());
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
                entity.getScore(),
                entity.isConnected(),
                entity.getJoinedAt(),
                entity.getLeftAt());
    }

    private static SessionQuestionEntity toEntity(SessionQuestion question) {
        SessionQuestionEntity entity = new SessionQuestionEntity();
        entity.setId(question.getId());
        entity.setSessionId(question.getSessionId());
        entity.setSourceQuestionId(question.getSourceQuestionId());
        entity.setOrderIndex(question.getOrderIndex());
        entity.setPoints(question.getPoints());
        entity.setTimeLimitSeconds(question.getTimeLimitSeconds());
        entity.setTitle(question.getTitle());
        entity.setDescription(question.getDescription());
        entity.setQuestionType(question.getQuestionType());
        entity.setOpenedAt(question.getOpenedAt());
        entity.setClosedAt(question.getClosedAt());

        List<SessionAnswerOptionEntity> options = new ArrayList<>();
        for (SessionAnswerOption option : question.getOptions()) {
            SessionAnswerOptionEntity optionEntity = toEntity(option);
            optionEntity.setSessionQuestion(entity);
            options.add(optionEntity);
        }
        entity.setAnswerOptions(options);
        return entity;
    }

    private static SessionQuestion toDomain(SessionQuestionEntity entity) {
        List<SessionAnswerOption> options = entity.getAnswerOptions().stream()
                .map(GameSessionMapper::toDomain)
                .toList();
        return SessionQuestion.rehydrate(
                entity.getId(),
                entity.getSessionId(),
                entity.getSourceQuestionId(),
                entity.getOrderIndex(),
                entity.getPoints(),
                entity.getTimeLimitSeconds(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getQuestionType(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                options);
    }

    private static SessionAnswerOptionEntity toEntity(SessionAnswerOption option) {
        SessionAnswerOptionEntity entity = new SessionAnswerOptionEntity();
        entity.setId(option.getId());
        entity.setSessionQuestionId(option.getSessionQuestionId());
        entity.setSourceAnswerOptionId(option.getSourceAnswerOptionId());
        entity.setText(option.getText());
        entity.setCorrect(option.isCorrect());
        entity.setOrderIndex(option.getOrderIndex());
        return entity;
    }

    private static SessionAnswerOption toDomain(SessionAnswerOptionEntity entity) {
        return SessionAnswerOption.rehydrate(
                entity.getId(),
                entity.getSessionQuestionId(),
                entity.getSourceAnswerOptionId(),
                entity.getText(),
                entity.isCorrect(),
                entity.getOrderIndex());
    }

    private static PlayerAnswerEntity toEntity(PlayerAnswer answer) {
        PlayerAnswerEntity entity = new PlayerAnswerEntity();
        entity.setId(answer.getId());
        entity.setSessionQuestionId(answer.getSessionQuestionId());
        entity.setSessionPlayerId(answer.getSessionPlayerId());
        entity.setSessionAnswerOptionId(answer.getSessionAnswerOptionId());
        entity.setCorrect(answer.isCorrect());
        entity.setResponseTimeMs(answer.getResponseTimeMs());
        entity.setAwardedPoints(answer.getAwardedPoints());
        entity.setAnsweredAt(answer.getAnsweredAt());
        return entity;
    }

    private static PlayerAnswer toDomain(PlayerAnswerEntity entity) {
        return PlayerAnswer.rehydrate(
                entity.getId(),
                entity.getSessionQuestionId(),
                entity.getSessionPlayerId(),
                entity.getSessionAnswerOptionId(),
                entity.isCorrect(),
                entity.getResponseTimeMs(),
                entity.getAwardedPoints(),
                entity.getAnsweredAt());
    }
}
