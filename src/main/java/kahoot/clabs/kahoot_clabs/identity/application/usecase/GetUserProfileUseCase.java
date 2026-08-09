package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import org.springframework.stereotype.Service;

import kahoot.clabs.kahoot_clabs.identity.application.dto.UserProfileResponse;
import kahoot.clabs.kahoot_clabs.identity.application.port.UserReadPort;
import kahoot.clabs.kahoot_clabs.identity.application.query.GetUserProfileQuery;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;

@Service
public class GetUserProfileUseCase {

    private final UserReadPort userReadPort;

    public GetUserProfileUseCase(UserReadPort userReadPort) {
        this.userReadPort = userReadPort;
    }

    public UserProfileResponse execute(GetUserProfileQuery query) {
        return userReadPort.findById(query.userId())
                .map(UserProfileResponse::from)
                .orElseThrow(() -> new UserNotFoundException(query.userId()));
    }
}
