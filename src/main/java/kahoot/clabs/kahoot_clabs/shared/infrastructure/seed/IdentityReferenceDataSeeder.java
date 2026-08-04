package kahoot.clabs.kahoot_clabs.shared.infrastructure.seed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class IdentityReferenceDataSeeder implements DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(IdentityReferenceDataSeeder.class);

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
        return "identity-reference-data";
    }

    @Override
    public void seed() {
        Map<String, Permission> permissions = seedPermissions();
        seedRoles(permissions);
        seedUsersByRole();
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

        List<Permission> persisted = new ArrayList<>();
        for (PermissionSeed definition : definitions) {
            persisted.add(ensurePermission(definition.name(), definition.description(), definition.module()));
        }
        return persisted.stream().collect(java.util.stream.Collectors.toMap(Permission::getName, p -> p));
    }

    private Permission ensurePermission(String name, String description, String module) {
        return permissionRepository.findByNameAndModule(name, module)
                .orElseGet(() -> permissionRepository.save(Permission.create(name, description, module)));
    }

    private void seedRoles(Map<String, Permission> permissions) {
        ensureRole(
                RoleType.ADMIN,
                "Administrator",
                "Creador de la plataforma con acceso total",
                List.of(permissions.get("PLATFORM_FULL_ACCESS")));

        ensureRole(
                RoleType.OWNER_ORGANIZATION,
                "Organization Owner",
                "Poder sobre su organización: quizzes, miembros, statuses y sesiones",
                List.of(
                        permissions.get("ORGANIZATION_EDIT"),
                        permissions.get("ORGANIZATION_STATUS_ASSIGN"),
                        permissions.get("MEMBER_MANAGE"),
                        permissions.get("MEMBER_STATUS_ASSIGN"),
                        permissions.get("QUIZ_CREATE"),
                        permissions.get("QUIZ_EDIT"),
                        permissions.get("QUIZ_ANSWER_EDIT"),
                        permissions.get("SESSION_JOIN_ANYTIME"),
                        permissions.get("PROFILE_EDIT")));

        ensureRole(
                RoleType.RH_ORGANIZATION,
                "Organization HR",
                "Gestión de quizzes, estados y miembros; acceso a sesiones en cualquier momento",
                List.of(
                        permissions.get("QUIZ_CREATE"),
                        permissions.get("QUIZ_EDIT"),
                        permissions.get("QUIZ_STATUS_CHANGE"),
                        permissions.get("QUESTION_STATUS_CHANGE"),
                        permissions.get("ANSWER_STATUS_CHANGE"),
                        permissions.get("MEMBER_STATUS_ASSIGN"),
                        permissions.get("SESSION_JOIN_ANYTIME"),
                        permissions.get("PROFILE_EDIT")));

        ensureRole(
                RoleType.COMMON_MEMBER,
                "Common Member",
                "Editar perfil e ingresar a sesiones solo cuando estén habilitadas",
                List.of(
                        permissions.get("PROFILE_EDIT"),
                        permissions.get("SESSION_JOIN_WHEN_ENABLED")));
    }

    private void ensureRole(RoleType type, String name, String description, List<Permission> expected) {
        List<Permission> safeExpected = expected.stream().filter(p -> p != null).toList();
        roleRepository.findByType(type).ifPresentOrElse(
                existing -> syncPermissions(existing, safeExpected),
                () -> {
                    Role role = Role.create(name, type, description);
                    safeExpected.forEach(role::addPermission);
                    roleRepository.save(role);
                    log.info("Created role {}", type);
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
            log.info("Synced permissions for role {}", role.getType());
        }
    }

    private void seedUsersByRole() {
        SeedProperties.Admin admin = seedProperties.getAdmin();
        String password = admin.getPassword();

        ensureUserWithRole(
                RoleType.ADMIN,
                admin.getEmail(),
                admin.getFirstName(),
                admin.getLastName(),
                password);

        ensureUserWithRole(
                RoleType.OWNER_ORGANIZATION,
                "owner@kahoot-clabs.local",
                "Org",
                "Owner",
                password);

        ensureUserWithRole(
                RoleType.RH_ORGANIZATION,
                "rh@kahoot-clabs.local",
                "Org",
                "HR",
                password);

        ensureUserWithRole(
                RoleType.COMMON_MEMBER,
                "member@kahoot-clabs.local",
                "Common",
                "Member",
                password);
    }

    private void ensureUserWithRole(
            RoleType roleType,
            String email,
            String firstName,
            String lastName,
            String rawPassword) {
        Role role = roleRepository.findByType(roleType)
                .orElseThrow(() -> new IllegalStateException(roleType + " role must exist before seeding users"));

        userRepository.findByEmail(email).ifPresentOrElse(
                existing -> {
                    if (existing.getRoleId() == null || !existing.getRoleId().equals(role.getId())) {
                        existing.changeRole(role.getId());
                        userRepository.save(existing);
                        log.info("Assigned {} role to existing user {}", roleType, email);
                    }
                },
                () -> {
                    Password.assertValidRaw(rawPassword);
                    User user = User.create(
                            email,
                            firstName,
                            lastName,
                            Password.fromHashed(passwordHasher.hash(rawPassword)));
                    user.changeRole(role.getId());
                    userRepository.save(user);
                    log.info("Created seed user {} with role {}", email, roleType);
                });
    }
}
