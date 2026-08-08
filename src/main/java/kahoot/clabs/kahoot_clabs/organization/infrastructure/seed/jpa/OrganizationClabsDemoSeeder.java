package kahoot.clabs.kahoot_clabs.organization.infrastructure.seed.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.identity.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.Role;
import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.Password;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;
import kahoot.clabs.kahoot_clabs.organization.application.port.OrganizationCatalogProjectionPort;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationDepartmentEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.jpa.OrganizationJobEntity;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationDepartmentJpaRepository;
import kahoot.clabs.kahoot_clabs.organization.infrastructure.repository.jpa.OrganizationJobJpaRepository;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.SeedProperties;

/**
 * Demo tenant "Clabs" with IT catalogs and members.
 * Runs after identity seed (roles/users) and organization status catalogs.
 */
@Component
public class OrganizationClabsDemoSeeder implements DataSeeder {

    private static final String ORG_NAME = "Clabs";
    private static final String ORG_SLUG = "clabs";

    private final OrganizationRepository organizationRepository;
    private final OrganizationDepartmentJpaRepository departmentRepository;
    private final OrganizationJobJpaRepository jobRepository;
    // private final ObjectProvider<OrganizationCatalogProjectionPort> catalogProjectionPort;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final SeedProperties seedProperties;

    public OrganizationClabsDemoSeeder(
            OrganizationRepository organizationRepository,
            OrganizationDepartmentJpaRepository departmentRepository,
            OrganizationJobJpaRepository jobRepository,
            // ObjectProvider<OrganizationCatalogProjectionPort> catalogProjectionPort,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher,
            SeedProperties seedProperties
        ) {
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
        this.jobRepository = jobRepository;
        // this.catalogProjectionPort = catalogProjectionPort;
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
        seedDepartments();
        seedJobs();
        seedClabsOrganization();
    }

    private void seedDepartments() {
        record CatalogSeed(String name, String description) {
        }

        List<CatalogSeed> definitions = List.of(
                new CatalogSeed("Ingeniería de Software", "Desarrollo y mantenimiento de productos"),
                new CatalogSeed("Infraestructura y Cloud", "Servidores, redes y ambientes cloud"),
                new CatalogSeed("QA y Testing", "Calidad, automatización y regresiones"),
                new CatalogSeed("Datos y Analytics", "Pipelines, BI y modelos de datos"),
                new CatalogSeed("Ciberseguridad", "Seguridad, accesos y cumplimiento"),
                new CatalogSeed("Soporte Técnico", "Mesa de ayuda y operaciones IT"),
                new CatalogSeed("Producto Digital", "Discovery, roadmap y UX"),
                new CatalogSeed("DevOps", "CI/CD, observabilidad y releases"));

        for (CatalogSeed definition : definitions) {
            OrganizationDepartmentEntity entity = new OrganizationDepartmentEntity();
            entity.setId(UUID.randomUUID());
            entity.setName(definition.name());
            entity.setDescription(truncate(definition.description(), 100));
            departmentRepository.save(entity);
            // catalogProjectionPort.ifAvailable(port -> port.saveDepartment(
                    // entity.getId(), entity.getName(), entity.getDescription()));
        }
    }

    private void seedJobs() {
        record CatalogSeed(String name, String description) {
        }

        List<CatalogSeed> definitions = List.of(
                new CatalogSeed("Software Engineer", "Diseña e implementa features backend/frontend"),
                new CatalogSeed("Tech Lead", "Lidera equipo técnico y estándares de código"),
                new CatalogSeed("DevOps Engineer", "Automatiza despliegues y monitoreo"),
                new CatalogSeed("QA Automation Engineer", "Pruebas automatizadas y calidad continua"),
                new CatalogSeed("Data Engineer", "Construye pipelines y modelos de datos"),
                new CatalogSeed("Security Analyst", "Evalúa riesgos y controles de seguridad"),
                new CatalogSeed("IT Support Specialist", "Resuelve incidencias y soporte a usuarios"),
                new CatalogSeed("Product Manager", "Define prioridades y valor de producto"),
                new CatalogSeed("UX Designer", "Diseña experiencias e interfaces"),
                new CatalogSeed("SRE", "Confiabilidad, SLO y respuesta a incidentes"));

        for (CatalogSeed definition : definitions) {
            OrganizationJobEntity entity = new OrganizationJobEntity();
            entity.setId(UUID.randomUUID());
            entity.setName(definition.name());
            entity.setDescription(truncate(definition.description(), 100));
            jobRepository.save(entity);
            // catalogProjectionPort.ifAvailable(port -> port.saveJob(
            //         entity.getId(), entity.getName(), entity.getDescription()));
        }
    }

    private void seedClabsOrganization() {
        Role ownerRole = roleRepository.findByType(RoleType.OWNER_ORGANIZATION)
                .orElseThrow(() -> new IllegalStateException("OWNER_ORGANIZATION role must exist"));
        Role rhRole = roleRepository.findByType(RoleType.RH_ORGANIZATION)
                .orElseThrow(() -> new IllegalStateException("RH_ORGANIZATION role must exist"));
        Role memberRole = roleRepository.findByType(RoleType.COMMON_MEMBER)
                .orElseThrow(() -> new IllegalStateException("COMMON_MEMBER role must exist"));

        Organization organization = Organization.create(ORG_NAME, ORG_SLUG);
        organization.updateDetails(
                ORG_NAME,
                "Organización demo Clabs enfocada en productos y servicios de tecnología");

        // Users already created by identity seed
        organization.addMember(requireUser("owner@kahoot-clabs.local").getId(), ownerRole.getId());
        organization.addMember(requireUser("rh@kahoot-clabs.local").getId(), rhRole.getId());
        organization.addMember(requireUser("member@kahoot-clabs.local").getId(), memberRole.getId());

        String rawPassword = seedProperties.getAdmin().getPassword();
        Password.assertValidRaw(rawPassword);
        String hashedPassword = passwordHasher.hash(rawPassword);

        for (DemoPerson person : demoPeople()) {
            User user = User.create(
                    person.email(),
                    person.firstName(),
                    person.lastName(),
                    Password.fromHashed(hashedPassword));
            user.changeRole(memberRole.getId());
            User saved = userRepository.save(user);
            organization.addMember(saved.getId(), memberRole.getId());
        }

        organizationRepository.save(organization);
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "User '" + email + "' must exist before Clabs demo seed"));
    }

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

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    private record DemoPerson(String firstName, String lastName, String email) {
    }
}
