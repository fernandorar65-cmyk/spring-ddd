package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.gameplay.application.dto.GameSessionResponse;
import kahoot.clabs.kahoot_clabs.gameplay.application.port.GameSessionReadModelPort;
import kahoot.clabs.kahoot_clabs.gameplay.application.readmodel.GameSessionReadModel;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.SessionStatus;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class ListGameSessionsUseCase {

    private final GameSessionReadModelPort gameSessionReadModelPort;
    private final OrganizationRepository organizationRepository;

    public ListGameSessionsUseCase(
            GameSessionReadModelPort gameSessionReadModelPort,
            OrganizationRepository organizationRepository) {
        this.gameSessionReadModelPort = gameSessionReadModelPort;
        this.organizationRepository = organizationRepository;
    }

    public List<GameSessionResponse> execute(UUID organizationId, String statusCsv, UUID quizId) {
        GameSessionSupport.requireOrganization(organizationRepository, organizationId);

        List<GameSessionReadModel> sessions = gameSessionReadModelPort
                .findByOrganizationIdOrderByCreatedAtDesc(organizationId);

        if (statusCsv != null && !statusCsv.isBlank()) {
            Set<String> statuses = Arrays.stream(statusCsv.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(this::parseStatus)
                    .map(Enum::name)
                    .collect(Collectors.toSet());
            sessions = sessions.stream()
                    .filter(session -> statuses.contains(session.status()))
                    .toList();
        }

        if (quizId != null) {
            sessions = sessions.stream()
                    .filter(session -> quizId.equals(session.quizId()))
                    .toList();
        }

        return sessions.stream().map(GameSessionResponse::from).toList();
    }

    private SessionStatus parseStatus(String raw) {
        try {
            return SessionStatus.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Invalid session status: " + raw);
        }
    }
}
