package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.dto.UserProfileResponse;
import kahoot.clabs.kahoot_clabs.identity.application.query.GetUserProfileQuery;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;

@Service
public class GetUserProfileUseCase {

    private final UserRepository userRepository;

    public GetUserProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse execute(GetUserProfileQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(query.userId()));
        return UserProfileResponse.from(user);
    }
}
