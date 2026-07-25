package kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject;

public enum GameStatus {
    LOBBY("Esperando jugadores"),
    RUNNING("En curso"),
    FINISHED("Finalizada"),
    CANCELLED("Cancelada");

    private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
