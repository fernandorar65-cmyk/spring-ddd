package kahoot.clabs.kahoot_clabs.users.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.AggregateRoot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.users.domain.enums.RoleType;

public class Role extends AggregateRoot {

    private final UUID id;
    private String name;
    private final RoleType type;
    private String description;
    private final List<Permission> permissions = new ArrayList<>();

    private Role(UUID id, String name, RoleType type, String description) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name is required");
        }
        if (type == null) {
            throw new DomainException("Role type is required");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name.trim();
        this.type = type;
        this.description = description;
    }

    public static Role create(String name, RoleType type, String description) {
        return new Role(null, name, type, description);
    }

    public static Role rehydrate(UUID id, String name, RoleType type, String description, List<Permission> permissions) {
        Role role = new Role(id, name, type, description);
        if (permissions != null) {
            role.permissions.addAll(permissions);
        }
        return role;
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name is required");
        }
        this.name = name.trim();
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void addPermission(Permission permission) {
        if (permission == null) {
            throw new DomainException("Permission is required");
        }
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream().anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RoleType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }
}
