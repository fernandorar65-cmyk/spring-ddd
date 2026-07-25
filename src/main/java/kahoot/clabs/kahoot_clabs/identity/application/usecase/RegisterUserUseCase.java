package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.command.RegisterUserCommand;
import kahoot.clabs.kahoot_clabs.identity.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.identity.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.EmailAlreadyRegisteredException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AuthUserResponse execute(RegisterUserCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException(command.email());
        }

        Password.assertValidRaw(command.password());
        Password hashedPassword = Password.fromHashed(passwordHasher.hash(command.password()));

        User user = User.create(
                command.email(),
                command.firstName(),
                command.lastName(),
                hashedPassword);

        return AuthUserResponse.from(userRepository.save(user));
    }
}
