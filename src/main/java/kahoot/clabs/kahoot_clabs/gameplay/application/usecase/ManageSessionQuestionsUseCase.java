package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.HostActionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.OpenQuestionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class ManageSessionQuestionsUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationMembershipPort organizationMembershipPort;

    public ManageSessionQuestionsUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationMembershipPort organizationMembershipPort) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationMembershipPort = organizationMembershipPort;
    }

    @Transactional
    public GameSessionResponse open(UUID organizationId, UUID sessionId, OpenQuestionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.openQuestion(command.questionIndex());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse close(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.closeQuestion();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse next(UUID organizationId, UUID sessionId, HostActionCommand command) {
        GameSession session = loadForHost(organizationId, sessionId, command.hostUserId());
        session.nextQuestion();
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    private GameSession loadForHost(UUID organizationId, UUID sessionId, UUID hostUserId) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, hostUserId);
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.ensureHost(hostUserId);
        return session;
    }
}
