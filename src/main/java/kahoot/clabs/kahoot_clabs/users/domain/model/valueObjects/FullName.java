package kahoot.clabs.kahoot_clabs.users.domain.model.valueObjects;
import lombok.Getter;

@Getter
public class FullName {
    private final String firstName;
    private final String lastName;

    public FullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}