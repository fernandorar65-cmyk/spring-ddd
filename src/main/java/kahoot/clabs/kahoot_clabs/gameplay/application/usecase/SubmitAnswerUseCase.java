package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.SubmitAnswerCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.PlayerAnswerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;

@Service
public class SubmitAnswerUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationMembershipPort organizationMembershipPort;

    public SubmitAnswerUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationMembershipPort organizationMembershipPort) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationMembershipPort = organizationMembershipPort;
    }

    @Transactional
    public PlayerAnswerResponse execute(UUID organizationId, UUID sessionId, SubmitAnswerCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        PlayerAnswer answer = session.submitAnswer(command.userId(), command.sessionAnswerOptionId());
        gameSessionRepository.save(session);
        return PlayerAnswerResponse.from(answer);
    }
}
