package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.JoinSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.LeaveSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.command.UpdateNicknameCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.SessionPlayerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class ManageSessionPlayersUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationMembershipPort organizationMembershipPort;

    public ManageSessionPlayersUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationMembershipPort organizationMembershipPort) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationMembershipPort = organizationMembershipPort;
    }

    @Transactional
    public GameSessionResponse join(UUID organizationId, UUID sessionId, JoinSessionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.join(command.userId(), command.nickname());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse leave(UUID organizationId, UUID sessionId, LeaveSessionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.leave(command.userId());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public SessionPlayerResponse updateNickname(
            UUID organizationId, UUID sessionId, UpdateNicknameCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.changeNickname(command.userId(), command.nickname());
        GameSession saved = gameSessionRepository.save(session);
        return SessionPlayerResponse.from(saved.findPlayerByUserId(command.userId()).orElseThrow());
    }
}
