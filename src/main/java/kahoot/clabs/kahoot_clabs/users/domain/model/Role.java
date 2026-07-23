package kahoot.clabs.kahoot_clabs.users.domain.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.users.domain.model.Enums.RoleType;

@Getter
public class Role {

    private final UUID id;
    private String name;
    private RoleType type;
    private String description;

    private List<Permission> permissions = new ArrayList<>();   // ← Nueva

    private Role(UUID id, String name, RoleType type, String description) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public static Role create(String name, RoleType type, String description) {
        return new Role(null, name, type, description);
    }

    // Métodos de dominio
    public void addPermission(Permission permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }
}