package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.command.UpdateProfileCommand;
import kahoot.clabs.kahoot_clabs.identity.application.dto.UserProfileResponse;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.UserProfile;

@Service
public class UpdateProfileUseCase {

    private final UserRepository userRepository;

    public UpdateProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserProfileResponse execute(UUID userId, UpdateProfileCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateProfile(UserProfile.builder()
                .department(command.department())
                .jobTitle(command.jobTitle())
                .phoneNumber(command.phoneNumber())
                .birthDate(command.birthDate())
                .bio(command.bio())
                .location(command.location())
                .avatarUrl(command.avatarUrl())
                .build());

        return UserProfileResponse.from(userRepository.save(user));
    }
}
