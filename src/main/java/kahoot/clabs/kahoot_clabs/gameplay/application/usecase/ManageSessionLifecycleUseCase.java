package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.HostActionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class ManageSessionLifecycleUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationRepository organizationRepository;
    private final QuizRepository quizRepository;

    public ManageSessionLifecycleUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationRepository organizationRepository,
            QuizRepository quizRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationRepository = organizationRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional
    public GameSessionResponse start(UUID organizationId, UUID sessionId, HostActionCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.hostUserId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(command.hostUserId());

        if (session.getQuestions().isEmpty()) {
            Quiz quiz = quizRepository.findById(session.getQuizId())
                    .orElseThrow(() -> new DomainException("Quiz not found: " + session.getQuizId()));
            GameSessionSupport.freezeFromQuiz(session, quiz);
        }
        session.start();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse cancel(UUID organizationId, UUID sessionId, HostActionCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.hostUserId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(command.hostUserId());
        session.cancel();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse finish(UUID organizationId, UUID sessionId, HostActionCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.hostUserId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(command.hostUserId());
        session.finish();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
