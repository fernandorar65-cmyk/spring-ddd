package kahoot.clabs.kahoot_clabs.identity.infrastructure.seed.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.identity.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.entity.Permission;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.PermissionRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.SeedProperties;

@Component
public class IdentityReferenceDataSeeder implements DataSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final SeedProperties seedProperties;

    public IdentityReferenceDataSeeder(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            SeedProperties seedProperties) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.seedProperties = seedProperties;
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public String name() {
        return "seed-basic-data-for-identity";
    }

    @Override
    public void seed() {
        Map<String, Permission> permissions = seedPermissions();
        Map<RoleType, Role> roles = seedRoles(permissions);
        seedUsers(roles);
    }

    private Map<String, Permission> seedPermissions() {
        record PermissionSeed(String name, String description, String module) {
        }

        List<PermissionSeed> definitions = List.of(
                new PermissionSeed("PLATFORM_FULL_ACCESS", "Acceso total a la plataforma", "platform"),
                new PermissionSeed("ORGANIZATION_EDIT", "Editar organización", "organization"),
                new PermissionSeed("ORGANIZATION_STATUS_ASSIGN", "Asignar status de organización", "organization"),
                new PermissionSeed("MEMBER_MANAGE", "Gestionar miembros de organización", "organization"),
                new PermissionSeed("MEMBER_STATUS_ASSIGN", "Asignar status de miembros", "organization"),
                new PermissionSeed("QUIZ_CREATE", "Crear quizzes", "quiz"),
                new PermissionSeed("QUIZ_EDIT", "Editar quizzes", "quiz"),
                new PermissionSeed("QUIZ_STATUS_CHANGE", "Cambiar estatus de quizzes", "quiz"),
                new PermissionSeed("QUIZ_ANSWER_EDIT", "Editar respuestas de quizzes", "quiz"),
                new PermissionSeed("QUESTION_STATUS_CHANGE", "Cambiar estado de preguntas", "quiz"),
                new PermissionSeed("ANSWER_STATUS_CHANGE", "Cambiar estado de respuestas", "quiz"),
                new PermissionSeed("PROFILE_EDIT", "Editar datos del propio perfil", "user"),
                new PermissionSeed("SESSION_JOIN_ANYTIME", "Ingresar a sesiones en cualquier momento", "session"),
                new PermissionSeed(
                        "SESSION_JOIN_WHEN_ENABLED",
                        "Ingresar a sesiones solo cuando estén habilitadas",
                        "session"));

        Map<String, Permission> byName = new HashMap<>();
        for (PermissionSeed definition : definitions) {
            Permission saved = permissionRepository.save(
                    Permission.create(definition.name(), definition.description(), definition.module()));
            byName.put(saved.getName(), saved);
        }
        return byName;
    }

    private Map<RoleType, Role> seedRoles(Map<String, Permission> permissions) {
        record RoleSeed(RoleType type, String name, String description, List<String> permissionNames) {}

        List<RoleSeed> definitions = List.of(
                new RoleSeed(
                        RoleType.ADMIN,
                        "Administrator",
                        "Creador de la plataforma con acceso total",
                        List.of("PLATFORM_FULL_ACCESS")),
                new RoleSeed(
                        RoleType.OWNER_ORGANIZATION,
                        "Organization Owner",
                        "Poder sobre su organización: quizzes, miembros, statuses y sesiones",
                        List.of(
                                "ORGANIZATION_EDIT",
                                "ORGANIZATION_STATUS_ASSIGN",
                                "MEMBER_MANAGE",
                                "MEMBER_STATUS_ASSIGN",
                                "QUIZ_CREATE",
                                "QUIZ_EDIT",
                                "QUIZ_ANSWER_EDIT",
                                "SESSION_JOIN_ANYTIME",
                                "PROFILE_EDIT")),
                new RoleSeed(
                        RoleType.RH_ORGANIZATION,
                        "Organization HR",
                        "Gestión de quizzes, estados y miembros; acceso a sesiones en cualquier momento",
                        List.of(
                                "QUIZ_CREATE",
                                "QUIZ_EDIT",
                                "QUIZ_STATUS_CHANGE",
                                "QUESTION_STATUS_CHANGE",
                                "ANSWER_STATUS_CHANGE",
                                "MEMBER_STATUS_ASSIGN",
                                "SESSION_JOIN_ANYTIME",
                                "PROFILE_EDIT")),
                new RoleSeed(
                        RoleType.COMMON_MEMBER,
                        "Common Member",
                        "Editar perfil e ingresar a sesiones solo cuando estén habilitadas",
                        List.of("PROFILE_EDIT", "SESSION_JOIN_WHEN_ENABLED")));

        Map<RoleType, Role> byType = new HashMap<>();
        for (RoleSeed definition : definitions) {
            Role role = Role.create(definition.name(), definition.type(), definition.description());
            for (String permissionName : definition.permissionNames()) {
                Permission permission = permissions.get(permissionName);
                if (permission != null) {
                    role.addPermission(permission);
                }
            }
            Role saved = roleRepository.save(role);
            byType.put(saved.getType(), saved);
        }
        return byType;
    }

    private void seedUsers(Map<RoleType, Role> roles) {
        SeedProperties.Admin admin = seedProperties.getAdmin();
        String rawPassword = admin.getPassword();
        Password.assertValidRaw(rawPassword);
        String hashedPassword = passwordHasher.hash(rawPassword);

        record UserSeed(RoleType roleType, String email, String firstName, String lastName) {
        }

        List<UserSeed> definitions = List.of(
                new UserSeed(RoleType.ADMIN, admin.getEmail(), admin.getFirstName(), admin.getLastName()),
                new UserSeed(RoleType.OWNER_ORGANIZATION, "owner@kahoot-clabs.local", "Org", "Owner"),
                new UserSeed(RoleType.RH_ORGANIZATION, "rh@kahoot-clabs.local", "Org", "HR"),
                new UserSeed(RoleType.COMMON_MEMBER, "member@kahoot-clabs.local", "Common", "Member"));

        for (UserSeed definition : definitions) {
            Role role = roles.get(definition.roleType());
            User user = User.create(
                    definition.email(),
                    definition.firstName(),
                    definition.lastName(),
                    Password.fromHashed(hashedPassword));
            user.changeRole(role.getId());
            userRepository.save(user);
        }
    }
}
