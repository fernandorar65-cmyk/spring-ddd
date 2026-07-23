package kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects;

import lombok.Getter;

@Getter
public class Email {
    private final String value;

    public Email(String value) {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.value = value;
    }
}
