package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.SubmitAnswerCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.PlayerAnswerResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;

@Service
public class SubmitAnswerUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationRepository organizationRepository;

    public SubmitAnswerUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationRepository organizationRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public PlayerAnswerResponse execute(UUID organizationId, UUID sessionId, SubmitAnswerCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.userId());
        GameSession session = GameSessionSupport.requireSession(gameSessionRepository, organizationId, sessionId);
        PlayerAnswer answer = session.submitAnswer(command.userId(), command.sessionAnswerOptionId());
        gameSessionRepository.save(session);
        return PlayerAnswerResponse.from(answer);
    }
}
