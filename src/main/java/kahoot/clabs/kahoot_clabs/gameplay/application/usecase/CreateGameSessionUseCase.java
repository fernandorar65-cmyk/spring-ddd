package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.CreateGameSessionCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class CreateGameSessionUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationRepository organizationRepository;
    private final QuizRepository quizRepository;

    public CreateGameSessionUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationRepository organizationRepository,
            QuizRepository quizRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationRepository = organizationRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional
    public GameSessionResponse execute(UUID organizationId, CreateGameSessionCommand command) {
        Organization organization = GameSessionSupport.requireOrganization(organizationRepository, organizationId);
        GameSessionSupport.requireMember(organization, command.hostUserId());

        Quiz quiz = quizRepository.findById(command.quizId())
                .orElseThrow(() -> new DomainException("Quiz not found: " + command.quizId()));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to organization: " + organizationId);
        }
        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new DomainException("Only published quizzes can be used to create a session");
        }

        GameSession session = GameSession.create(organizationId, quiz.getId(), command.hostUserId());
        GameSessionSupport.freezeFromQuiz(session, quiz);
        return GameSessionResponse.from(gameSessionRepository.save(session));
    }
}
