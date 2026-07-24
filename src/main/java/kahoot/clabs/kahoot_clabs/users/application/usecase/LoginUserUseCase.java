package kahoot.clabs.kahoot_clabs.users.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.users.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.users.application.dto.LoginRequest;
import kahoot.clabs.kahoot_clabs.users.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.users.domain.model.User;
import kahoot.clabs.kahoot_clabs.users.domain.repository.UserRepository;

@Service
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AuthUserResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new DomainException("Invalid email or password"));

        if (!passwordHasher.matches(request.password(), user.getPassword().hashedValue())) {
            throw new DomainException("Invalid email or password");
        }

        user.recordLogin();
        user = userRepository.save(user);

        return new AuthUserResponse(
                user.getId(),
                user.getOrganizationId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName());
    }
}
