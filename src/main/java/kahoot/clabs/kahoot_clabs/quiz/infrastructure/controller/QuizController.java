package kahoot.clabs.kahoot_clabs.quiz.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.quiz.application.command.CreateQuizCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.AnswerOptionCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.QuestionAssetCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.ReorderAnswerOptionsCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.UpdateQuizCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.QuestionCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.UpdateQuestionCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.ReorderQuestionsCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.DuplicateQuizCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.CreateQuizUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.GetQuizUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ListQuizzesUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.EditQuizContentUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.UpdateQuizUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ManageQuizCategoriesUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ManageQuizQuestionsUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ManageQuizLifecycleUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.UploadQuizImageUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/quizzes")
@Tag(name = "Quizzes", description = "Gestión de quizzes, preguntas, opciones, assets y ciclo de vida")
public class QuizController {

    private final CreateQuizUseCase createQuizUseCase;
    private final GetQuizUseCase getQuizUseCase;
    private final ListQuizzesUseCase listQuizzesUseCase;
    private final EditQuizContentUseCase editQuizContentUseCase;
    private final UpdateQuizUseCase updateQuizUseCase;
    private final ManageQuizCategoriesUseCase manageQuizCategoriesUseCase;
    private final ManageQuizQuestionsUseCase manageQuizQuestionsUseCase;
    private final ManageQuizLifecycleUseCase manageQuizLifecycleUseCase;
    private final UploadQuizImageUseCase uploadQuizImageUseCase;

    public QuizController(
            CreateQuizUseCase createQuizUseCase,
            GetQuizUseCase getQuizUseCase,
            ListQuizzesUseCase listQuizzesUseCase,
            EditQuizContentUseCase editQuizContentUseCase,
            UpdateQuizUseCase updateQuizUseCase,
            ManageQuizCategoriesUseCase manageQuizCategoriesUseCase,
            ManageQuizQuestionsUseCase manageQuizQuestionsUseCase,
            ManageQuizLifecycleUseCase manageQuizLifecycleUseCase,
            UploadQuizImageUseCase uploadQuizImageUseCase) {
        this.createQuizUseCase = createQuizUseCase;
        this.getQuizUseCase = getQuizUseCase;
        this.listQuizzesUseCase = listQuizzesUseCase;
        this.editQuizContentUseCase = editQuizContentUseCase;
        this.updateQuizUseCase = updateQuizUseCase;
        this.manageQuizCategoriesUseCase = manageQuizCategoriesUseCase;
        this.manageQuizQuestionsUseCase = manageQuizQuestionsUseCase;
        this.manageQuizLifecycleUseCase = manageQuizLifecycleUseCase;
        this.uploadQuizImageUseCase = uploadQuizImageUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear quiz", description = "Crea un quiz en borrador dentro de la organización indicada.")
    public ResponseEntity<ApiResponse<QuizResponse>> create(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Valid @RequestBody CreateQuizCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED, "Quiz created", createQuizUseCase.execute(organizationId, command)));
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "Obtener quiz", description = "Devuelve el detalle completo del quiz (preguntas, opciones, assets) si pertenece a la organización.")
    public ResponseEntity<ApiResponse<QuizResponse>> getById(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Quiz retrieved", getQuizUseCase.execute(organizationId, quizId)));
    }

    @PutMapping("/{quizId}")
    @Operation(summary = "Actualizar quiz", description = "Actualiza metadatos del quiz (título, descripción, dificultad, visibilidad, settings).")
    public ResponseEntity<ApiResponse<QuizResponse>> update(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Valid @RequestBody UpdateQuizCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Quiz updated",
                updateQuizUseCase.execute(organizationId, quizId, command)));
    }

    @GetMapping
    @Operation(summary = "Listar quizzes", description = "Lista todos los quizzes de la organización.")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> list(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Quizzes retrieved", listQuizzesUseCase.execute(organizationId)));
    }

    @PostMapping("/{quizId}/categories/{categoryId}")
    @Operation(summary = "Asignar categoría", description = "Asocia una categoría existente al quiz.")
    public ResponseEntity<ApiResponse<QuizResponse>> assignCategory(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la categoría") @PathVariable UUID categoryId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Category assigned to quiz",
                manageQuizCategoriesUseCase.assign(organizationId, quizId, categoryId)));
    }

    @DeleteMapping("/{quizId}/categories/{categoryId}")
    @Operation(summary = "Quitar categoría", description = "Desasocia una categoría del quiz.")
    public ResponseEntity<Void> removeCategory(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la categoría") @PathVariable UUID categoryId) {
        manageQuizCategoriesUseCase.remove(organizationId, quizId, categoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions")
    @Operation(summary = "Agregar pregunta", description = "Agrega una nueva pregunta al quiz con tipo, puntos y tiempo límite.")
    public ResponseEntity<ApiResponse<QuizResponse>> addQuestion(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Valid @RequestBody QuestionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Question added",
                manageQuizQuestionsUseCase.add(organizationId, quizId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    @Operation(summary = "Actualizar pregunta", description = "Actualiza el contenido o configuración de una pregunta existente.")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuestion(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Valid @RequestBody UpdateQuestionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question updated",
                manageQuizQuestionsUseCase.update(organizationId, quizId, questionId, command)));
    }

    @PutMapping("/{quizId}/questions/order")
    @Operation(summary = "Reordenar preguntas", description = "Define el orden de las preguntas dentro del quiz.")
    public ResponseEntity<ApiResponse<QuizResponse>> reorderQuestions(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Valid @RequestBody ReorderQuestionsCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Questions reordered",
                manageQuizQuestionsUseCase.reorder(organizationId, quizId, command)));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    @Operation(summary = "Eliminar pregunta", description = "Elimina una pregunta del quiz.")
    public ResponseEntity<Void> removeQuestion(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId) {
        manageQuizQuestionsUseCase.remove(organizationId, quizId, questionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/publish")
    @Operation(summary = "Publicar quiz", description = "Publica el quiz si cumple reglas de negocio (preguntas listas, etc.). Solo quizzes publicados pueden usarse en partidas.")
    public ResponseEntity<ApiResponse<QuizResponse>> publish(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Quiz published",
                manageQuizLifecycleUseCase.publish(organizationId, quizId)));
    }

    @PostMapping("/{quizId}/archive")
    @Operation(summary = "Archivar quiz", description = "Archiva el quiz para que deje de estar disponible para nuevas partidas.")
    public ResponseEntity<ApiResponse<QuizResponse>> archive(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Quiz archived",
                manageQuizLifecycleUseCase.archive(organizationId, quizId)));
    }

    @PostMapping("/{quizId}/duplicate")
    @Operation(summary = "Duplicar quiz", description = "Crea una copia del quiz (preguntas y opciones) como nuevo borrador.")
    public ResponseEntity<ApiResponse<QuizResponse>> duplicate(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Valid @RequestBody DuplicateQuizCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Quiz duplicated",
                manageQuizLifecycleUseCase.duplicate(organizationId, quizId, command)));
    }

    @PostMapping("/{quizId}/questions/{questionId}/options")
    @Operation(summary = "Agregar opción de respuesta", description = "Agrega una opción de respuesta a una pregunta.")
    public ResponseEntity<ApiResponse<QuizResponse>> addAnswerOption(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Valid @RequestBody AnswerOptionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Answer option added",
                editQuizContentUseCase.addAnswerOption(organizationId, quizId, questionId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}/options/{optionId}")
    @Operation(summary = "Actualizar opción de respuesta", description = "Actualiza el texto o si la opción es correcta.")
    public ResponseEntity<ApiResponse<QuizResponse>> updateAnswerOption(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Parameter(description = "Identificador de la opción") @PathVariable UUID optionId,
            @Valid @RequestBody AnswerOptionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Answer option updated",
                editQuizContentUseCase.updateAnswerOption(organizationId, quizId, questionId, optionId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}/options/order")
    @Operation(summary = "Reordenar opciones", description = "Define el orden de las opciones de una pregunta.")
    public ResponseEntity<ApiResponse<QuizResponse>> reorderAnswerOptions(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Valid @RequestBody ReorderAnswerOptionsCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Answer options reordered",
                editQuizContentUseCase.reorderAnswerOptions(organizationId, quizId, questionId, command)));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}/options/{optionId}")
    @Operation(summary = "Eliminar opción de respuesta", description = "Elimina una opción de una pregunta.")
    public ResponseEntity<Void> removeAnswerOption(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Parameter(description = "Identificador de la opción") @PathVariable UUID optionId) {
        editQuizContentUseCase.removeAnswerOption(organizationId, quizId, questionId, optionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions/{questionId}/assets")
    @Operation(summary = "Agregar asset por URL", description = "Adjunta un asset (imagen/video/audio) a la pregunta usando una URL ya conocida.")
    public ResponseEntity<ApiResponse<QuizResponse>> addAsset(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Valid @RequestBody QuestionAssetCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Question asset added",
                editQuizContentUseCase.addAsset(organizationId, quizId, questionId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}/assets/{assetId}")
    @Operation(summary = "Actualizar asset", description = "Actualiza metadatos o URL de un asset de la pregunta.")
    public ResponseEntity<ApiResponse<QuizResponse>> updateAsset(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Parameter(description = "Identificador del asset") @PathVariable UUID assetId,
            @Valid @RequestBody QuestionAssetCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question asset updated",
                editQuizContentUseCase.updateAsset(organizationId, quizId, questionId, assetId, command)));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}/assets/{assetId}")
    @Operation(summary = "Eliminar asset", description = "Elimina un asset de la pregunta.")
    public ResponseEntity<Void> removeAsset(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Parameter(description = "Identificador del asset") @PathVariable UUID assetId) {
        editQuizContentUseCase.removeAsset(organizationId, quizId, questionId, assetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{quizId}/questions/{questionId}/assets/images", consumes = "multipart/form-data")
    @Operation(
            summary = "Subir imagen a S3",
            description = "Sube un archivo de imagen (JPEG/PNG/WebP/GIF, máx. 10 MB) a S3 y lo adjunta como asset de la pregunta.")
    public ResponseEntity<ApiResponse<QuizResponse>> uploadImage(
            @Parameter(description = "Identificador de la organización") @PathVariable UUID organizationId,
            @Parameter(description = "Identificador del quiz") @PathVariable UUID quizId,
            @Parameter(description = "Identificador de la pregunta") @PathVariable UUID questionId,
            @Parameter(description = "Archivo de imagen") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Texto alternativo opcional") @RequestParam(required = false) String altText)
            throws java.io.IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Image uploaded",
                uploadQuizImageUseCase.execute(
                        organizationId,
                        quizId,
                        questionId,
                        file.getBytes(),
                        file.getContentType(),
                        file.getOriginalFilename(),
                        altText)));
    }
}
