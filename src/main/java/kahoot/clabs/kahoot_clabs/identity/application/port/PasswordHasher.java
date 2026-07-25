package kahoot.clabs.kahoot_clabs.identity.application.port;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
