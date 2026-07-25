package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.command.LoginCommand;
import kahoot.clabs.kahoot_clabs.identity.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.identity.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.InvalidCredentialsException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;

@Service
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AuthUserResponse execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(command.password(), user.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        user.recordLogin();
        return AuthUserResponse.from(userRepository.save(user));
    }
}
