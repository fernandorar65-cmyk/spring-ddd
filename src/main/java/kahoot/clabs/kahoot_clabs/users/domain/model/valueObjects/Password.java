package kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects;
import lombok.Getter;

@Getter
public class Password {

    private final String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Password create(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener mínimo 8 caracteres");
        }
        String hashed = hash(rawPassword);
        return new Password(hashed);
    }

    private static String hash(String rawPassword) {
        // TODO: Reemplazar con BCrypt o Argon2 en producción
        return rawPassword; // Placeholder
    }

    public boolean matches(String rawPassword) {
        return this.hashedValue.equals(hash(rawPassword));
    }
}