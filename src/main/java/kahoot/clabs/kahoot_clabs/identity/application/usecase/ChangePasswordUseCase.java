package kahoot.clabs.kahoot_clabs.identity.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.command.ChangePasswordCommand;
import kahoot.clabs.kahoot_clabs.identity.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.InvalidCredentialsException;
import kahoot.clabs.kahoot_clabs.identity.domain.exception.UserNotFoundException;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;

@Service
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public ChangePasswordUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public void execute(UUID userId, ChangePasswordCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordHasher.matches(command.currentPassword(), user.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        Password.assertValidRaw(command.newPassword());
        user.changePassword(Password.fromHashed(passwordHasher.hash(command.newPassword())));
        userRepository.save(user);
    }
}
