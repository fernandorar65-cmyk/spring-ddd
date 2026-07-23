package kahoot.clabs.kahoot_clabs.quizzes.domain.enums;

public enum QuizStatus {
    DRAFT("Borrador"),
    PUBLISHED("Publicado"),
    ARCHIVED("Archivado");

    private final String description;

    QuizStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
