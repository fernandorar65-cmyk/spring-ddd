package kahoot.clabs.kahoot_clabs.identity.domain.valueobject;

public enum UserStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    SUSPENDED("Suspendido"),
    PENDING("Pendiente de activación");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
