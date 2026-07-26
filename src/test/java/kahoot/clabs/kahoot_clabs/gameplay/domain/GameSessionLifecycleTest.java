package kahoot.clabs.kahoot_clabs.gameplay.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

class GameSessionLifecycleTest {

    @Test
    void evaluates_an_answer_against_the_session_option_and_awards_snapshot_points() {
        GameSession session = sessionWithTwoQuestions();
        var player = session.join(UUID.randomUUID(), "Ana");
        session.start();
        var question = session.currentQuestion().orElseThrow();
        var correctOption = question.getOptions().stream().filter(option -> option.isCorrect()).findFirst().orElseThrow();

        var answer = session.submitAnswer(player.getId(), question.getId(), correctOption.getId());

        assertThat(answer.isCorrect()).isTrue();
        assertThat(answer.getAwardedPoints()).isEqualTo(question.getPoints());
        assertThat(player.getScore().value()).isEqualTo(question.getPoints());
    }

    @Test
    void rejects_an_option_that_does_not_belong_to_the_current_question() {
        GameSession session = sessionWithTwoQuestions();
        var player = session.join(UUID.randomUUID(), "Ana");
        session.start();
        var currentQuestion = session.currentQuestion().orElseThrow();
        var optionFromOtherQuestion = session.getQuestions().get(1).getOptions().getFirst();

        assertThatThrownBy(() -> session.submitAnswer(player.getId(), currentQuestion.getId(), optionFromOtherQuestion.getId()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void permits_only_one_answer_per_player_and_question() {
        GameSession session = sessionWithTwoQuestions();
        var player = session.join(UUID.randomUUID(), "Ana");
        session.start();
        var question = session.currentQuestion().orElseThrow();
        var option = question.getOptions().getFirst();
        session.submitAnswer(player.getId(), question.getId(), option.getId());

        assertThatThrownBy(() -> session.submitAnswer(player.getId(), question.getId(), option.getId()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already answered");
    }

    @Test
    void requires_closing_before_advancing_and_finishing() {
        GameSession session = sessionWithTwoQuestions();
        session.join(UUID.randomUUID(), "Ana");
        session.start();

        assertThatThrownBy(session::nextQuestion)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("must be closed");
        assertThatThrownBy(session::finish)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("must be closed");

        session.closeCurrentQuestion();
        session.nextQuestion();

        assertThat(session.currentQuestion().orElseThrow().isOpen()).isTrue();
    }

    @Test
    void ranks_players_by_score_then_correct_answers_then_response_time() {
        GameSession session = sessionWithTwoQuestions();
        var firstPlayer = session.join(UUID.randomUUID(), "Ana");
        var secondPlayer = session.join(UUID.randomUUID(), "Beto");
        session.start();
        var question = session.currentQuestion().orElseThrow();
        var correctOption = question.getOptions().stream().filter(option -> option.isCorrect()).findFirst().orElseThrow();
        var incorrectOption = question.getOptions().stream().filter(option -> !option.isCorrect()).findFirst().orElseThrow();

        session.submitAnswer(firstPlayer.getId(), question.getId(), incorrectOption.getId());
        session.submitAnswer(secondPlayer.getId(), question.getId(), correctOption.getId());

        assertThat(session.leaderboard()).extracting(rank -> rank.playerId())
                .containsExactly(secondPlayer.getId(), firstPlayer.getId());
    }

    private GameSession sessionWithTwoQuestions() {
        GameSession session = GameSession.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        session.addQuestionSnapshot(
                UUID.randomUUID(), "Primera", null, "MULTIPLE_CHOICE", 500, 20,
                options());
        session.addQuestionSnapshot(
                UUID.randomUUID(), "Segunda", null, "MULTIPLE_CHOICE", 1_000, 20,
                options());
        return session;
    }

    private List<AnswerOptionSnapshot> options() {
        return List.of(
                new AnswerOptionSnapshot(UUID.randomUUID(), "Correcta", true, 1),
                new AnswerOptionSnapshot(UUID.randomUUID(), "Incorrecta", false, 2));
    }
}
