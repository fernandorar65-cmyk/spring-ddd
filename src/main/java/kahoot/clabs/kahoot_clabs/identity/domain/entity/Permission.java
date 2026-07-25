package kahoot.clabs.kahoot_clabs.identity.domain.entity;

import java.util.Objects;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public class Permission {

    private final UUID id;
    private final String name;
    private final String description;
    private final String module;

    private Permission(UUID id, String name, String description, String module) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Permission name is required");
        }
        if (module == null || module.isBlank()) {
            throw new DomainException("Permission module is required");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name.trim().toUpperCase();
        this.description = description;
        this.module = module.trim().toLowerCase();
    }

    public static Permission create(String name, String description, String module) {
        return new Permission(null, name, description, module);
    }

    public static Permission reconstitute(UUID id, String name, String description, String module) {
        return new Permission(id, name, description, module);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModule() {
        return module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission that)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, module);
    }

    public static final class Common {
        public static final Permission QUIZ_CREATE = Permission.create("QUIZ_CREATE", "Crear quizzes", "quiz");
        public static final Permission QUIZ_PUBLISH = Permission.create("QUIZ_PUBLISH", "Publicar quizzes", "quiz");
        public static final Permission GAME_HOST = Permission.create("GAME_HOST", "Iniciar sesiones de juego", "gameplay");
        public static final Permission USER_MANAGE = Permission.create("USER_MANAGE", "Gestionar usuarios", "user");

        private Common() {
        }
    }
}
