package kahoot.clabs.kahoot_clabs.shared.infrastructure.seed;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.identity.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationDepartmentEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.OrganizationJobEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.OrganizationDepartmentJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.OrganizationJobJpaRepository;

/**
 * Demo tenant "Clabs" with IT catalogs and mixed male/female members.
 * Runs after identity roles/users and organization status catalogs.
 */
@Component
public class OrganizationClabsDemoSeeder implements DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(OrganizationClabsDemoSeeder.class);

    private static final String ORG_NAME = "Clabs";
    private static final String ORG_SLUG = "clabs";

    private final OrganizationRepository organizationRepository;
    private final OrganizationDepartmentJpaRepository departmentRepository;
    private final OrganizationJobJpaRepository jobRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final SeedProperties seedProperties;

    public OrganizationClabsDemoSeeder(
            OrganizationRepository organizationRepository,
            OrganizationDepartmentJpaRepository departmentRepository,
            OrganizationJobJpaRepository jobRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            SeedProperties seedProperties) {
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
        this.seedProperties = seedProperties;
    }

    @Override
    public int order() {
        return 30;
    }

    @Override
    public String name() {
        return "organization-clabs-demo";
    }

    @Override
    public void seed() {
        seedItDepartments();
        seedItJobs();
        seedClabsOrganization();
    }

    private void seedItDepartments() {
        List.of(
                catalog("Ingeniería de Software", "Desarrollo y mantenimiento de productos"),
                catalog("Infraestructura y Cloud", "Servidores, redes y ambientes cloud"),
                catalog("QA y Testing", "Calidad, automatización y regresiones"),
                catalog("Datos y Analytics", "Pipelines, BI y modelos de datos"),
                catalog("Ciberseguridad", "Seguridad, accesos y cumplimiento"),
                catalog("Soporte Técnico", "Mesa de ayuda y operaciones IT"),
                catalog("Producto Digital", "Discovery, roadmap y UX"),
                catalog("DevOps", "CI/CD, observabilidad y releases")
        ).forEach(item -> ensureDepartment(item.name(), item.description()));
    }

    private void seedItJobs() {
        List.of(
                catalog("Software Engineer", "Diseña e implementa features backend/frontend"),
                catalog("Tech Lead", "Lidera equipo técnico y estándares de código"),
                catalog("DevOps Engineer", "Automatiza despliegues y monitoreo"),
                catalog("QA Automation Engineer", "Pruebas automatizadas y calidad continua"),
                catalog("Data Engineer", "Construye pipelines y modelos de datos"),
                catalog("Security Analyst", "Evalúa riesgos y controles de seguridad"),
                catalog("IT Support Specialist", "Resuelve incidencias y soporte a usuarios"),
                catalog("Product Manager", "Define prioridades y valor de producto"),
                catalog("UX Designer", "Diseña experiencias e interfaces"),
                catalog("SRE", "Confiabilidad, SLO y respuesta a incidentes")
        ).forEach(item -> ensureJob(item.name(), item.description()));
    }

    private void seedClabsOrganization() {
        Role ownerRole = requireRole(RoleType.OWNER_ORGANIZATION);
        Role rhRole = requireRole(RoleType.RH_ORGANIZATION);
        Role memberRole = requireRole(RoleType.COMMON_MEMBER);

        Organization organization = organizationRepository.findBySlug(ORG_SLUG)
                .orElseGet(() -> {
                    Organization created = Organization.create(ORG_NAME, ORG_SLUG);
                    created.updateDetails(
                            ORG_NAME,
                            "Organización demo Clabs enfocada en productos y servicios de tecnología");
                    log.info("Created organization {} ({})", ORG_NAME, ORG_SLUG);
                    return created;
                });

        ensureMember(organization, "owner@kahoot-clabs.local", "Org", "Owner", ownerRole.getId());
        ensureMember(organization, "rh@kahoot-clabs.local", "Org", "HR", rhRole.getId());
        ensureMember(organization, "member@kahoot-clabs.local", "Common", "Member", memberRole.getId());

        for (DemoPerson person : demoPeople()) {
            ensureMember(
                    organization,
                    person.email(),
                    person.firstName(),
                    person.lastName(),
                    memberRole.getId());
        }

        organizationRepository.save(organization);
        log.info(
                "Seeded Clabs organization with {} member(s)",
                organization.getMembers().size());
    }

    private void ensureMember(
            Organization organization,
            String email,
            String firstName,
            String lastName,
            UUID roleId) {
        User user = ensureUser(email, firstName, lastName, roleId);
        if (!organization.hasMember(user.getId())) {
            organization.addMember(user.getId(), roleId);
            log.info("Added member {} to {}", email, ORG_SLUG);
        }
    }

    private User ensureUser(String email, String firstName, String lastName, UUID roleId) {
        return userRepository.findByEmail(email).map(existing -> {
            if (existing.getRoleId() == null || !existing.getRoleId().equals(roleId)) {
                existing.changeRole(roleId);
                return userRepository.save(existing);
            }
            return existing;
        }).orElseGet(() -> {
            String rawPassword = seedProperties.getAdmin().getPassword();
            Password.assertValidRaw(rawPassword);
            User user = User.create(
                    email,
                    firstName,
                    lastName,
                    Password.fromHashed(passwordHasher.hash(rawPassword)));
            user.changeRole(roleId);
            User saved = userRepository.save(user);
            log.info("Created Clabs demo user {}", email);
            return saved;
        });
    }

    private Role requireRole(RoleType type) {
        return roleRepository.findByType(type)
                .orElseThrow(() -> new IllegalStateException(type + " role must exist before Clabs demo seed"));
    }

    private void ensureDepartment(String name, String description) {
        if (departmentRepository.existsByName(name)) {
            return;
        }
        OrganizationDepartmentEntity entity = new OrganizationDepartmentEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription(truncate(description, 100));
        departmentRepository.save(entity);
        log.info("Created department {}", name);
    }

    private void ensureJob(String name, String description) {
        if (jobRepository.existsByName(name)) {
            return;
        }
        OrganizationJobEntity entity = new OrganizationJobEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription(truncate(description, 100));
        jobRepository.save(entity);
        log.info("Created job {}", name);
    }

    /**
     * Fixed “random-looking” Spanish names (miembros y miembras) for idempotent seeding.
     */
    private static List<DemoPerson> demoPeople() {
        return List.of(
                new DemoPerson("Valentina", "Ríos", "valentina.rios@clabs.local"),
                new DemoPerson("Camila", "Vargas", "camila.vargas@clabs.local"),
                new DemoPerson("Sofía", "Mendoza", "sofia.mendoza@clabs.local"),
                new DemoPerson("Isabella", "Castro", "isabella.castro@clabs.local"),
                new DemoPerson("Mariana", "Paredes", "mariana.paredes@clabs.local"),
                new DemoPerson("Lucía", "Herrera", "lucia.herrera@clabs.local"),
                new DemoPerson("Andrés", "Salazar", "andres.salazar@clabs.local"),
                new DemoPerson("Mateo", "Guzmán", "mateo.guzman@clabs.local"),
                new DemoPerson("Santiago", "Ortega", "santiago.ortega@clabs.local"),
                new DemoPerson("Diego", "Navarro", "diego.navarro@clabs.local"),
                new DemoPerson("Julián", "Peña", "julian.pena@clabs.local"),
                new DemoPerson("Sebastián", "Rojas", "sebastian.rojas@clabs.local"));
    }

    private static CatalogItem catalog(String name, String description) {
        return new CatalogItem(name, description);
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    private record CatalogItem(String name, String description) {
    }

    private record DemoPerson(String firstName, String lastName, String email) {
    }
}
