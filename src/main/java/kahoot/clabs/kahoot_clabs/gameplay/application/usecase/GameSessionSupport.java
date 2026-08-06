package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.AnswerOption;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

final class GameSessionSupport {

    private GameSessionSupport() {
    }

    static Organization requireOrganization(OrganizationRepository organizationRepository, UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
    }

    static void requireMember(Organization organization, UUID userId) {
        if (!organization.hasMember(userId)) {
            throw new DomainException("User is not a member of this organization: " + userId);
        }
    }

    static GameSession requireSession(
            GameSessionRepository sessionRepository,
            UUID organizationId,
            UUID sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(sessionId));
        session.ensureBelongsTo(organizationId);
        return session;
    }

    static void freezeFromQuiz(GameSession session, Quiz quiz) {
        if (!session.getQuestions().isEmpty()) {
            return;
        }
        List<Question> sourceQuestions = quiz.getQuestions().stream()
                .sorted(Comparator.comparingInt(Question::getOrderIndex))
                .toList();
        if (sourceQuestions.isEmpty()) {
            throw new DomainException("Quiz has no questions to freeze");
        }
        AtomicInteger index = new AtomicInteger(0);
        List<SessionQuestion> frozen = sourceQuestions.stream()
                .map(question -> toFrozenQuestion(session.getId(), question, index.getAndIncrement()))
                .toList();
        session.freezeQuestions(frozen);
    }

    private static SessionQuestion toFrozenQuestion(UUID sessionId, Question question, int zeroBasedIndex) {
        List<AnswerOption> sortedOptions = question.getOptions().stream()
                .sorted(Comparator.comparingInt(AnswerOption::getOrderIndex))
                .toList();
        AtomicInteger optionIndex = new AtomicInteger(0);
        List<SessionAnswerOption> frozenOptions = sortedOptions.stream()
                .map(option -> SessionAnswerOption.freeze(
                        null,
                        option.getId(),
                        option.getText(),
                        option.isCorrect(),
                        optionIndex.getAndIncrement()))
                .toList();
        return SessionQuestion.freeze(
                sessionId,
                question.getId(),
                zeroBasedIndex,
                question.getPoints().value(),
                question.getTimeLimit().seconds(),
                question.getTitle(),
                question.getDescription(),
                question.getType().name(),
                frozenOptions);
    }
}
