package kahoot.clabs.kahoot_clabs.quiz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.MediaType;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.QuestionType;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

class QuizContentManagementTest {

    @Test
    void manages_answer_options_only_through_the_quiz_aggregate() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.addAnswerOption(question.getId(), "Bogotá", true);
        quiz.addAnswerOption(question.getId(), "Medellín", false);

        UUID bogotaId = question.getOptions().getFirst().getId();
        UUID medellinId = question.getOptions().getLast().getId();
        quiz.updateAnswerOption(question.getId(), medellinId, "Cali", false);
        quiz.reorderAnswerOptions(question.getId(), List.of(medellinId, bogotaId));

        assertThat(question.getOptions()).extracting(option -> option.getText(), option -> option.getOrderIndex())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Cali", 1),
                        org.assertj.core.groups.Tuple.tuple("Bogotá", 2));

        quiz.removeAnswerOption(question.getId(), bogotaId);

        assertThat(question.getOptions()).singleElement()
                .extracting(option -> option.getOrderIndex())
                .isEqualTo(1);
    }

    @Test
    void rejects_an_incomplete_or_repeated_option_order() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.addAnswerOption(question.getId(), "Bogotá", true);
        quiz.addAnswerOption(question.getId(), "Cali", false);

        UUID optionId = question.getOptions().getFirst().getId();

        assertThatThrownBy(() -> quiz.reorderAnswerOptions(question.getId(), List.of(optionId, optionId)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void updates_and_removes_the_question_asset_through_the_quiz() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        Question question = quiz.addQuestion("Capital de Colombia", QuestionType.MULTIPLE_CHOICE);
        quiz.attachAsset(question.getId(), MediaType.IMAGE, "https://example.com/old.webp");
        UUID assetId = question.getAsset().getId();

        quiz.updateQuestionAsset(
                question.getId(),
                assetId,
                MediaType.IMAGE,
                "https://example.com/new.webp",
                "https://example.com/thumb.webp",
                "Mapa de Colombia",
                null);

        assertThat(question.getAsset().getUrl().value()).isEqualTo("https://example.com/new.webp");
        assertThat(question.getAsset().getAltText()).isEqualTo("Mapa de Colombia");

        quiz.removeQuestionAsset(question.getId(), assetId);

        assertThat(question.getAsset()).isNull();
    }

    @Test
    void does_not_allow_archived_quizzes_to_be_modified() {
        Quiz quiz = Quiz.create(UUID.randomUUID(), "Geografía", UUID.randomUUID());
        quiz.archive();

        assertThatThrownBy(() -> quiz.changeDifficulty(QuizDifficulty.HARD))
                .isInstanceOf(DomainException.class);
    }
}
