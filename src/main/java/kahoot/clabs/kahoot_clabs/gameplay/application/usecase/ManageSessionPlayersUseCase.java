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
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

@Service
public class ManageSessionPlayersUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationRepository organizationRepository;

    public ManageSessionPlayersUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationRepository organizationRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public GameSessionResponse join(UUID organizationId, UUID sessionId, JoinSessionCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.join(command.userId(), command.nickname());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public GameSessionResponse leave(UUID organizationId, UUID sessionId, LeaveSessionCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.leave(command.userId());
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }

    @Transactional
    public SessionPlayerResponse updateNickname(
            UUID organizationId, UUID sessionId, UpdateNicknameCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        session.changeNickname(command.userId(), command.nickname());
        GameSession saved = gameSessionRepository.save(session);
        return SessionPlayerResponse.from(saved.findPlayerByUserId(command.userId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<SessionPlayerResponse> listPlayers(UUID organizationId, UUID sessionId) {
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        return session.getPlayers().stream().map(SessionPlayerResponse::from).toList();
    }
}
