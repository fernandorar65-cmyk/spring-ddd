package kahoot.clabs.kahoot_clabs.quiz.infrastructure.seed;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.identity.domain.aggregate.User;
import kahoot.clabs.kahoot_clabs.identity.domain.repository.UserRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Category;
import kahoot.clabs.kahoot_clabs.quiz.domain.entity.Question;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.EstimatedTime;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.seed.DataSeeder;

@Component
public class QuizClabsDemoSeeder implements DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(QuizClabsDemoSeeder.class);

    private static final String ORG_SLUG = "clabs";
    private static final String OWNER_EMAIL = "owner@kahoot-clabs.local";

    private static final String QUIZ_JAVA = "Fundamentos de Java";
    private static final String QUIZ_CULTURE = "Cultura y trabajo en Clabs";
    private static final String QUIZ_DEVOPS = "DevOps esencial";

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    public QuizClabsDemoSeeder(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            QuizRepository quizRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public int order() {
        return 40;
    }

    @Override
    public String name() {
        return "quiz-clabs-demo";
    }

    @Override
    public void seed() {
        Organization organization = organizationRepository.findBySlug(ORG_SLUG)
                .orElseThrow(() -> new IllegalStateException(
                        "Organization '" + ORG_SLUG + "' must exist before quiz demo seed"));
        User owner = userRepository.findByEmail(OWNER_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "User '" + OWNER_EMAIL + "' must exist before quiz demo seed"));

        Category technology = ensureCategory(
                organization.getId(),
                "Tecnología",
                "Quizzes técnicos de programación y plataformas",
                "#2563EB",
                "code");
        Category culture = ensureCategory(
                organization.getId(),
                "Cultura",
                "Valores, colaboración y dinámicas de equipo",
                "#DB2777",
                "users");
        Category devops = ensureCategory(
                organization.getId(),
                "DevOps",
                "CI/CD, cloud y operaciones",
                "#059669",
                "cloud");

        ensureJavaQuiz(organization.getId(), owner.getId(), technology.getId());
        ensureCultureQuiz(organization.getId(), owner.getId(), culture.getId());
        ensureDevOpsQuiz(organization.getId(), owner.getId(), devops.getId(), technology.getId());

        log.info("Seeded Clabs quiz demo content for organization {}", ORG_SLUG);
    }

    private Category ensureCategory(
            UUID organizationId,
            String name,
            String description,
            String color,
            String icon) {
        return categoryRepository.findByOrganizationId(organizationId).stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Category category = Category.create(organizationId, name);
                    category.changeDescription(description);
                    category.changeColor(color);
                    category.changeIcon(icon);
                    Category saved = categoryRepository.save(category);
                    log.info("Created category {}", name);
                    return saved;
                });
    }

    private void ensureJavaQuiz(UUID organizationId, UUID createdById, UUID technologyCategoryId) {
        if (quizExists(organizationId, QUIZ_JAVA)) {
            return;
        }

        Quiz quiz = Quiz.create(organizationId, QUIZ_JAVA, createdById);
        quiz.changeDescription("Quiz base sobre conceptos esenciales de Java para onboarding técnico.");
        quiz.changeDifficulty(QuizDifficulty.EASY);
        quiz.changeEstimatedTime(EstimatedTime.ofMinutes(10));
        quiz.changeThumbnail("https://cdn.kahoot-clabs.local/seed/java-quiz.png");
        quiz.addCategory(technologyCategoryId);

        Question q1 = quiz.addQuestion("¿Qué palabra clave declara una constante en Java?", QuestionType.MULTIPLE_CHOICE);
        quiz.updateQuestion(
                q1.getId(),
                q1.getTitle(),
                "Selecciona la opción correcta.",
                QuizDifficulty.EASY,
                1000,
                20);
        quiz.addAnswerOption(q1.getId(), "final", true);
        quiz.addAnswerOption(q1.getId(), "const", false);
        quiz.addAnswerOption(q1.getId(), "static", false);
        quiz.addAnswerOption(q1.getId(), "immutable", false);
        quiz.attachAsset(
                q1.getId(),
                MediaType.IMAGE,
                "https://cdn.kahoot-clabs.local/seed/java-final.png",
                "https://cdn.kahoot-clabs.local/seed/java-final-thumb.png",
                "Palabra clave final en Java",
                null);

        Question q2 = quiz.addQuestion("En Java, String es un tipo primitivo.", QuestionType.TRUE_FALSE);
        quiz.updateQuestion(
                q2.getId(),
                q2.getTitle(),
                "Verdadero o falso.",
                QuizDifficulty.EASY,
                800,
                15);
        quiz.addAnswerOption(q2.getId(), "Verdadero", false);
        quiz.addAnswerOption(q2.getId(), "Falso", true);

        Question q3 = quiz.addQuestion(
                "¿Cuáles de estos son interfaces de colección en Java?",
                QuestionType.MULTIPLE_SELECT);
        quiz.updateQuestion(
                q3.getId(),
                q3.getTitle(),
                "Puedes marcar más de una opción.",
                QuizDifficulty.MODERATE,
                1200,
                25);
        quiz.addAnswerOption(q3.getId(), "List", true);
        quiz.addAnswerOption(q3.getId(), "Set", true);
        quiz.addAnswerOption(q3.getId(), "Array", false);
        quiz.addAnswerOption(q3.getId(), "Map", true);

        quiz.publish();
        quizRepository.save(quiz);
        log.info("Created published quiz '{}'", QUIZ_JAVA);
    }

    private void ensureCultureQuiz(UUID organizationId, UUID createdById, UUID cultureCategoryId) {
        if (quizExists(organizationId, QUIZ_CULTURE)) {
            return;
        }

        Quiz quiz = Quiz.create(organizationId, QUIZ_CULTURE, createdById);
        quiz.changeDescription("Dinámica corta sobre colaboración y cultura de equipo en Clabs.");
        quiz.changeDifficulty(QuizDifficulty.EASY);
        quiz.changeEstimatedTime(EstimatedTime.ofMinutes(5));
        quiz.addCategory(cultureCategoryId);

        Question q1 = quiz.addQuestion(
                "¿Qué práctica ayuda más a un buen code review?",
                QuestionType.MULTIPLE_CHOICE);
        quiz.updateQuestion(
                q1.getId(),
                q1.getTitle(),
                "Elige la mejor práctica.",
                QuizDifficulty.EASY,
                1000,
                20);
        quiz.addAnswerOption(q1.getId(), "Dar feedback concreto y respetuoso", true);
        quiz.addAnswerOption(q1.getId(), "Aprobar sin leer para ir más rápido", false);
        quiz.addAnswerOption(q1.getId(), "Criticar a la persona, no al código", false);
        quiz.addAnswerOption(q1.getId(), "Evitar preguntar dudas", false);

        Question q2 = quiz.addQuestion(
                "Documentar decisiones técnicas facilita el onboarding.",
                QuestionType.TRUE_FALSE);
        quiz.updateQuestion(
                q2.getId(),
                q2.getTitle(),
                null,
                QuizDifficulty.EASY,
                800,
                15);
        quiz.addAnswerOption(q2.getId(), "Verdadero", true);
        quiz.addAnswerOption(q2.getId(), "Falso", false);

        Question q3 = quiz.addQuestion(
                "Nombra una forma breve de pedir ayuda a tu equipo.",
                QuestionType.SHORT_ANSWER);
        quiz.updateQuestion(
                q3.getId(),
                q3.getTitle(),
                "Respuesta libre (evaluación manual / host).",
                QuizDifficulty.EASY,
                600,
                30);

        quiz.publish();
        quizRepository.save(quiz);
        log.info("Created published quiz '{}'", QUIZ_CULTURE);
    }

    private void ensureDevOpsQuiz(
            UUID organizationId,
            UUID createdById,
            UUID devopsCategoryId,
            UUID technologyCategoryId) {
        if (quizExists(organizationId, QUIZ_DEVOPS)) {
            return;
        }

        Quiz quiz = Quiz.create(organizationId, QUIZ_DEVOPS, createdById);
        quiz.changeDescription("Conceptos base de CI/CD y operación de servicios.");
        quiz.changeDifficulty(QuizDifficulty.MODERATE);
        quiz.changeEstimatedTime(EstimatedTime.ofMinutes(8));
        quiz.changeThumbnail("https://cdn.kahoot-clabs.local/seed/devops-quiz.png");
        quiz.addCategory(devopsCategoryId);
        quiz.addCategory(technologyCategoryId);

        Question q1 = quiz.addQuestion("¿Qué significa CI en CI/CD?", QuestionType.MULTIPLE_CHOICE);
        quiz.updateQuestion(
                q1.getId(),
                q1.getTitle(),
                null,
                QuizDifficulty.EASY,
                1000,
                20);
        quiz.addAnswerOption(q1.getId(), "Continuous Integration", true);
        quiz.addAnswerOption(q1.getId(), "Cloud Infrastructure", false);
        quiz.addAnswerOption(q1.getId(), "Container Isolation", false);
        quiz.addAnswerOption(q1.getId(), "Code Inspection", false);
        quiz.attachAsset(
                q1.getId(),
                MediaType.IMAGE,
                "https://cdn.kahoot-clabs.local/seed/cicd-pipeline.png",
                "https://cdn.kahoot-clabs.local/seed/cicd-pipeline-thumb.png",
                "Pipeline CI/CD",
                null);

        Question q2 = quiz.addQuestion(
                "Un rollback rápido es útil cuando un deploy introduce un incidente.",
                QuestionType.TRUE_FALSE);
        quiz.updateQuestion(
                q2.getId(),
                q2.getTitle(),
                null,
                QuizDifficulty.EASY,
                800,
                15);
        quiz.addAnswerOption(q2.getId(), "Verdadero", true);
        quiz.addAnswerOption(q2.getId(), "Falso", false);

        Question q3 = quiz.addQuestion(
                "¿Cuáles prácticas mejoran la confiabilidad de un servicio?",
                QuestionType.MULTIPLE_SELECT);
        quiz.updateQuestion(
                q3.getId(),
                q3.getTitle(),
                "Selecciona todas las que apliquen.",
                QuizDifficulty.MODERATE,
                1200,
                25);
        quiz.addAnswerOption(q3.getId(), "Monitoreo y alertas", true);
        quiz.addAnswerOption(q3.getId(), "Desplegar sin pruebas", false);
        quiz.addAnswerOption(q3.getId(), "Health checks", true);
        quiz.addAnswerOption(q3.getId(), "Feature flags", true);

        quiz.publish();
        quizRepository.save(quiz);
        log.info("Created published quiz '{}'", QUIZ_DEVOPS);
    }

    private boolean quizExists(UUID organizationId, String title) {
        return quizRepository.existsByOrganizationIdAndTitleIgnoreCase(organizationId, title);
    }
}
