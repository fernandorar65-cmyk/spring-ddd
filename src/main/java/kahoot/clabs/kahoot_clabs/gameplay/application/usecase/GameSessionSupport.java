package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.exception.GameSessionNotFoundException;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationNotFoundException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
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

    static void freezeFromSnapshot(GameSession session, PublishedQuizSnapshot snapshot) {
        if (!session.getQuestions().isEmpty()) {
            return;
        }
        List<QuestionSnapshot> sourceQuestions = snapshot.questions();
        if (sourceQuestions == null || sourceQuestions.isEmpty()) {
            throw new DomainException("Quiz has no questions to freeze");
        }
        List<QuestionSnapshot> ordered = sourceQuestions.stream()
                .sorted(Comparator.comparingInt(QuestionSnapshot::orderIndex))
                .toList();
        AtomicInteger index = new AtomicInteger(0);
        List<SessionQuestion> frozen = ordered.stream()
                .map(question -> toFrozenQuestion(session.getId(), question, index.getAndIncrement()))
                .toList();
        session.freezeQuestions(frozen);
    }

    private static SessionQuestion toFrozenQuestion(UUID sessionId, QuestionSnapshot question, int zeroBasedIndex) {
        List<AnswerOptionSnapshot> sortedOptions = question.options().stream()
                .sorted(Comparator.comparingInt(AnswerOptionSnapshot::orderIndex))
                .toList();
        AtomicInteger optionIndex = new AtomicInteger(0);
        List<SessionAnswerOption> frozenOptions = sortedOptions.stream()
                .map(option -> SessionAnswerOption.freeze(
                        null,
                        option.id(),
                        option.text(),
                        option.correct(),
                        optionIndex.getAndIncrement()))
                .toList();
        return SessionQuestion.freeze(
                sessionId,
                question.id(),
                zeroBasedIndex,
                question.points(),
                question.timeLimitSeconds(),
                question.title(),
                question.description(),
                question.type(),
                frozenOptions);
    }
}
