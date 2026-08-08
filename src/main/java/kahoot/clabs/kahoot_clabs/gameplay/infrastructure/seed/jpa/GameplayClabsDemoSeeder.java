package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.seed.jpa;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.QuizSnapshotPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionAnswerOption;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa.QuizEntity;
import kahoot.clabs.kahoot_clabs.gameplay.infrastructure.repository.jpa.SpringQuizJpaRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;

/**
 * Demo game sessions for Clabs. Runs after quiz demo seed.
 */
@Component
public class GameplayClabsDemoSeeder implements DataSeeder {

    private static final String ORG_SLUG = "clabs";
    private static final String OWNER_EMAIL = "owner@kahoot-clabs.local";

    private static final String QUIZ_JAVA = "Fundamentos de Java";
    private static final String QUIZ_CULTURE = "Cultura y trabajo en Clabs";
    private static final String QUIZ_DEVOPS = "DevOps esencial";

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final SpringQuizJpaRepository quizJpaRepository;
    private final QuizSnapshotPort quizSnapshotPort;
    private final GameSessionRepository gameSessionRepository;

    public GameplayClabsDemoSeeder(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            SpringQuizJpaRepository quizJpaRepository,
            QuizSnapshotPort quizSnapshotPort,
            GameSessionRepository gameSessionRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.quizJpaRepository = quizJpaRepository;
        this.quizSnapshotPort = quizSnapshotPort;
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public int order() {
        return 50;
    }

    @Override
    public String name() {
        return "gameplay-clabs-demo";
    }

    @Override
    public void seed() {
        Organization organization = organizationRepository.findBySlug(ORG_SLUG)
                .orElseThrow(() -> new IllegalStateException(
                        "Organization '" + ORG_SLUG + "' must exist before gameplay demo seed"));
        User owner = requireUser(OWNER_EMAIL);
        User member = requireUser("member@kahoot-clabs.local");
        User valentina = requireUser("valentina.rios@clabs.local");
        User andres = requireUser("andres.salazar@clabs.local");
        User camila = requireUser("camila.vargas@clabs.local");

        UUID orgId = organization.getId();
        UUID javaQuizId = requireQuizId(orgId, QUIZ_JAVA);
        UUID cultureQuizId = requireQuizId(orgId, QUIZ_CULTURE);
        UUID devopsQuizId = requireQuizId(orgId, QUIZ_DEVOPS);

        seedLobbySession(orgId, javaQuizId, owner, List.of(
                new Joiner(member, "Member"),
                new Joiner(valentina, "Vale"),
                new Joiner(andres, "Andres"),
                new Joiner(camila, "Cami")));

        seedFinishedSession(orgId, cultureQuizId, owner, List.of(
                new Joiner(member, "Member"),
                new Joiner(valentina, "Vale"),
                new Joiner(andres, "Andres")));

        seedCancelledSession(orgId, devopsQuizId, owner);
    }

    private void seedLobbySession(UUID orgId, UUID quizId, User host, List<Joiner> joiners) {
        GameSession session = createFrozenSession(orgId, quizId, host.getId());
        session = gameSessionRepository.save(session);

        for (Joiner joiner : joiners) {
            session.join(joiner.user().getId(), joiner.nickname());
        }
        gameSessionRepository.save(session);
    }

    private void seedFinishedSession(UUID orgId, UUID quizId, User host, List<Joiner> joiners) {
        GameSession session = createFrozenSession(orgId, quizId, host.getId());
        session = gameSessionRepository.save(session);

        for (Joiner joiner : joiners) {
            session.join(joiner.user().getId(), joiner.nickname());
        }

        session.start();
        while (true) {
            SessionQuestion current = session.findCurrentQuestion()
                    .orElseThrow(() -> new DomainException("Missing current question in seed"));
            UUID correctOptionId = current.getOptions().stream()
                    .filter(SessionAnswerOption::isCorrect)
                    .map(SessionAnswerOption::getId)
                    .findFirst()
                    .orElse(null);

            for (Joiner joiner : joiners) {
                session.submitAnswer(joiner.user().getId(), correctOptionId);
            }

            session.closeQuestion();
            int before = session.getCurrentQuestionIndex();
            session.nextQuestion();
            if (session.getStatus().isTerminal() || session.getCurrentQuestionIndex() == before) {
                break;
            }
        }

        if (!session.getStatus().isTerminal()) {
            session.finish();
        }
        gameSessionRepository.save(session);
    }

    private void seedCancelledSession(UUID orgId, UUID quizId, User host) {
        GameSession session = createFrozenSession(orgId, quizId, host.getId());
        session = gameSessionRepository.save(session);
        session.cancel();
        gameSessionRepository.save(session);
    }

    private GameSession createFrozenSession(UUID orgId, UUID quizId, UUID hostUserId) {
        PublishedQuizSnapshot snapshot = quizSnapshotPort
                .findPublishedByOrganizationAndId(orgId, quizId)
                .orElseThrow(() -> new IllegalStateException(
                        "Published quiz snapshot not found: " + quizId));
        GameSession session = GameSession.create(orgId, quizId, hostUserId);
        freezeFromSnapshot(session, snapshot);
        return session;
    }

    private static void freezeFromSnapshot(GameSession session, PublishedQuizSnapshot snapshot) {
        List<QuestionSnapshot> ordered = snapshot.questions().stream()
                .sorted(Comparator.comparingInt(QuestionSnapshot::orderIndex))
                .toList();
        AtomicInteger index = new AtomicInteger(0);
        List<SessionQuestion> frozen = ordered.stream()
                .map(question -> {
                    List<AnswerOptionSnapshot> sortedOptions = question.options().stream()
                            .sorted(Comparator.comparingInt(AnswerOptionSnapshot::orderIndex))
                            .toList();
                    AtomicInteger optionIndex = new AtomicInteger(0);
                    List<SessionAnswerOption> options = sortedOptions.stream()
                            .map(option -> SessionAnswerOption.freeze(
                                    null,
                                    option.id(),
                                    option.text(),
                                    option.correct(),
                                    optionIndex.getAndIncrement()))
                            .toList();
                    return SessionQuestion.freeze(
                            session.getId(),
                            question.id(),
                            index.getAndIncrement(),
                            question.points(),
                            question.timeLimitSeconds(),
                            question.title(),
                            question.description(),
                            question.type(),
                            options);
                })
                .toList();
        session.freezeQuestions(frozen);
    }

    private UUID requireQuizId(UUID organizationId, String title) {
        return quizJpaRepository
                .findFirstByOrganizationIdAndTitleIgnoreCase(organizationId, title)
                .map(QuizEntity::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Quiz '" + title + "' must exist before gameplay demo seed"));
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "User '" + email + "' must exist before gameplay demo seed"));
    }

    private record Joiner(User user, String nickname) {
    }
}
