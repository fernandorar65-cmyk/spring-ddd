package kahoot.clabs.kahoot_clabs.users.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.users.application.port.PasswordHasher;

@Component
public class BcryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
