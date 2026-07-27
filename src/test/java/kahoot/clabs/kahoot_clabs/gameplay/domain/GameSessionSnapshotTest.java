package kahoot.clabs.kahoot_clabs.gameplay.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;

class GameSessionSnapshotTest {

    @Test
    void keeps_a_complete_copy_of_the_question_and_its_options() {
        UUID originalQuestionId = UUID.randomUUID();
        UUID originalCorrectOptionId = UUID.randomUUID();
        UUID originalIncorrectOptionId = UUID.randomUUID();
        GameSession session = GameSession.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        SessionQuestion snapshot = session.addQuestionSnapshot(
                originalQuestionId,
                "¿Cuál es la capital de Colombia?",
                "Geografía básica",
                "MULTIPLE_CHOICE",
                1_000,
                20,
                List.of(
                        new AnswerOptionSnapshot(originalCorrectOptionId, "Bogotá", true, 1),
                        new AnswerOptionSnapshot(originalIncorrectOptionId, "Medellín", false, 2)));

        assertThat(snapshot.getQuizQuestionId()).isEqualTo(originalQuestionId);
        assertThat(snapshot.getTitle()).isEqualTo("¿Cuál es la capital de Colombia?");
        assertThat(snapshot.getDescription()).isEqualTo("Geografía básica");
        assertThat(snapshot.getQuestionType()).isEqualTo("MULTIPLE_CHOICE");
        assertThat(snapshot.getOptions())
                .extracting(option -> option.getOriginalAnswerOptionId(), option -> option.getText(), option -> option.isCorrect())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(originalCorrectOptionId, "Bogotá", true),
                        org.assertj.core.groups.Tuple.tuple(originalIncorrectOptionId, "Medellín", false));
    }
}
