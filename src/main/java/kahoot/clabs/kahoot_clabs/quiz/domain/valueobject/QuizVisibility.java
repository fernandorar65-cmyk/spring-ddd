package kahoot.clabs.kahoot_clabs.quiz.domain.valueobject;

public enum QuizVisibility {

    ORGANIZATION("Solo Organización", "Visible solo para usuarios de la empresa"),
    TEAM("Solo Equipo", "Visible solo para miembros de equipos específicos"),
    PRIVATE("Privado", "Solo visible para el creador y personas invitadas");

    private final String displayName;
    private final String description;

    QuizVisibility(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
