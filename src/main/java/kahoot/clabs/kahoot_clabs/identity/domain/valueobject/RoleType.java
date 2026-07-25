package kahoot.clabs.kahoot_clabs.identity.domain.valueobject;

public enum RoleType {
    ADMIN("Administrador"),
    MANAGER("Gestor"),
    INSTRUCTOR("Instructor"),
    EMPLOYEE("Empleado"),
    STUDENT("Estudiante");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
