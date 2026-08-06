package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.GameSession;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.GameSessionRepository;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class ListGameSessionsUseCase {

    private final GameSessionRepository gameSessionRepository;
    private final OrganizationRepository organizationRepository;

    public ListGameSessionsUseCase(
            GameSessionRepository gameSessionRepository,
            OrganizationRepository organizationRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public List<GameSessionResponse> execute(UUID organizationId, String statusCsv, UUID quizId) {
        GameSessionSupport.requireOrganization(organizationRepository, organizationId);

        List<GameSession> sessions;
        if (statusCsv != null && !statusCsv.isBlank()) {
            List<SessionStatus> statuses = Arrays.stream(statusCsv.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(this::parseStatus)
                    .toList();
            sessions = gameSessionRepository.findByOrganizationIdAndStatusIn(organizationId, statuses);
        } else if (quizId != null) {
            sessions = gameSessionRepository.findByOrganizationIdAndQuizId(organizationId, quizId);
        } else {
            sessions = gameSessionRepository.findByOrganizationId(organizationId);
        }

        if (quizId != null && statusCsv != null && !statusCsv.isBlank()) {
            sessions = sessions.stream().filter(session -> session.getQuizId().equals(quizId)).toList();
        }

        return sessions.stream().map(GameSessionResponse::from).toList();
    }

    private SessionStatus parseStatus(String raw) {
        try {
            return SessionStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Invalid session status: " + raw);
        }
    }
}
