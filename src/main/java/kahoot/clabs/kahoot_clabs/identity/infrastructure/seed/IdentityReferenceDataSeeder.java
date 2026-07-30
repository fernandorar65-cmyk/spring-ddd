package kahoot.clabs.kahoot_clabs.identity.infrastructure.seed;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.PermissionRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;

@Component
public class IdentityReferenceDataSeeder implements DataSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public IdentityReferenceDataSeeder(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public String name() {
        return "identity-reference-data";
    }

    @Override
    public void seed() {
        List<Permission> permissions = seedPermissions();
        seedAdminRole(permissions);
    }

    private List<Permission> seedPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(ensurePermission("QUIZ_CREATE", "Crear quizzes", "quiz"));
        permissions.add(ensurePermission("QUIZ_PUBLISH", "Publicar quizzes", "quiz"));
        permissions.add(ensurePermission("GAME_HOST", "Iniciar sesiones de juego", "gameplay"));
        permissions.add(ensurePermission("USER_MANAGE", "Gestionar usuarios", "user"));
        return permissions;
    }

    private Permission ensurePermission(String name, String description, String module) {
        return permissionRepository.findByNameAndModule(name, module)
                .orElseGet(() -> permissionRepository.save(Permission.create(name, description, module)));
    }

    private void seedAdminRole(List<Permission> permissions) {
        roleRepository.findByType(RoleType.ADMIN).ifPresentOrElse(
                existing -> syncPermissions(existing, permissions),
                () -> {
                    Role admin = Role.create("Administrator", RoleType.ADMIN, "Full access");
                    permissions.forEach(admin::addPermission);
                    roleRepository.save(admin);
                });
    }

    private void syncPermissions(Role role, List<Permission> expected) {
        boolean changed = false;
        for (Permission permission : expected) {
            if (!role.hasPermission(permission.getName())) {
                role.addPermission(permission);
                changed = true;
            }
        }
        if (changed) {
            roleRepository.save(role);
        }
    }
}
