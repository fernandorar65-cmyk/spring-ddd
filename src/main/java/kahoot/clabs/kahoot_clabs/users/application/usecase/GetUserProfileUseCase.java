package kahoot.clabs.kahoot_clabs.users.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.users.application.dto.UserProfileResponse;
import kahoot.clabs.kahoot_clabs.users.domain.model.User;
import kahoot.clabs.kahoot_clabs.users.domain.repository.UserRepository;

@Service
public class GetUserProfileUseCase {

    private final UserRepository userRepository;

    public GetUserProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse execute(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("User not found"));
        return new UserProfileResponse(
                user.getId(),
                user.getOrganizationId(),
                user.getRoleId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName(),
                user.getStatus().name());
    }
}
