package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.CreateGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.integration.OrganizationMembershipPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.mongo.QuizSnapshotPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class CreateGameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationMembershipPort organizationMembershipPort;
    private final QuizSnapshotPort quizSnapshotPort;

    public CreateGameSessionUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationMembershipPort organizationMembershipPort,
            QuizSnapshotPort quizSnapshotPort) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationMembershipPort = organizationMembershipPort;
        this.quizSnapshotPort = quizSnapshotPort;
    }

    @Transactional
    public GameSessionResponse execute(UUID organizationId, CreateGameSessionCommand command) {
        GameSessionSupport.requireOrganization(organizationMembershipPort, organizationId);
        GameSessionSupport.requireMember(organizationMembershipPort, organizationId, command.hostUserId());

        PublishedQuizSnapshot snapshot = quizSnapshotPort
                .findPublishedByOrganizationAndId(organizationId, command.quizId())
                .orElseThrow(() -> new DomainException(
                        "Published quiz not found for organization: " + command.quizId()));

        GameSession session = GameSession.create(organizationId, snapshot.quizId(), command.hostUserId());
        GameSessionSupport.freezeFromSnapshot(session, snapshot);
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
