package kahoot.clabs.kahoot_clabs.users.domain.model;
import lombok.Getter;
import java.util.UUID;

@Getter
public class Permission {

    private final UUID id;
    private final String name;           // Ej: QUIZ_CREATE, GAME_HOST, USER_MANAGE
    private final String description;
    private final String module;         // Ej: "quiz", "gameplay", "user", "report"

    private Permission(UUID id, String name, String description, String module) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.module = module;
    }

    public static Permission create(String name, String description, String module) {
        return new Permission(null, name, description, module);
    }

    // Algunos permisos comunes de ejemplo
    public static class Common {
        public static final Permission QUIZ_CREATE = Permission.create("QUIZ_CREATE", "Crear quizzes", "quiz");
        public static final Permission QUIZ_PUBLISH = Permission.create("QUIZ_PUBLISH", "Publicar quizzes", "quiz");
        public static final Permission GAME_HOST = Permission.create("GAME_HOST", "Iniciar sesiones de juego", "gameplay");
        public static final Permission USER_MANAGE = Permission.create("USER_MANAGE", "Gestionar usuarios", "user");
    }
}