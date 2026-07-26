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
    public ResponseEntity<ApiResponse<QuizResponse>> create(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateQuizCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED, "Quiz created", createQuizUseCase.execute(organizationId, command)));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponse>> getById(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Quiz retrieved", getQuizUseCase.execute(organizationId, quizId)));
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponse>> update(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @Valid @RequestBody UpdateQuizCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Quiz updated",
                updateQuizUseCase.execute(organizationId, quizId, command)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizResponse>>> list(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Quizzes retrieved", listQuizzesUseCase.execute(organizationId)));
    }

    @PostMapping("/{quizId}/categories/{categoryId}")
    public ResponseEntity<ApiResponse<QuizResponse>> assignCategory(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID categoryId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Category assigned to quiz",
                manageQuizCategoriesUseCase.assign(organizationId, quizId, categoryId)));
    }

    @DeleteMapping("/{quizId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategory(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID categoryId) {
        manageQuizCategoriesUseCase.remove(organizationId, quizId, categoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<ApiResponse<QuizResponse>> addQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @Valid @RequestBody QuestionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Question added",
                manageQuizQuestionsUseCase.add(organizationId, quizId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @Valid @RequestBody UpdateQuestionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question updated",
                manageQuizQuestionsUseCase.update(organizationId, quizId, questionId, command)));
    }

    @PutMapping("/{quizId}/questions/order")
    public ResponseEntity<ApiResponse<QuizResponse>> reorderQuestions(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @Valid @RequestBody ReorderQuestionsCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Questions reordered",
                manageQuizQuestionsUseCase.reorder(organizationId, quizId, command)));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<Void> removeQuestion(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId) {
        manageQuizQuestionsUseCase.remove(organizationId, quizId, questionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/publish")
    public ResponseEntity<ApiResponse<QuizResponse>> publish(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Quiz published",
                manageQuizLifecycleUseCase.publish(organizationId, quizId)));
    }

    @PostMapping("/{quizId}/archive")
    public ResponseEntity<ApiResponse<QuizResponse>> archive(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Quiz archived",
                manageQuizLifecycleUseCase.archive(organizationId, quizId)));
    }

    @PostMapping("/{quizId}/duplicate")
    public ResponseEntity<ApiResponse<QuizResponse>> duplicate(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @Valid @RequestBody DuplicateQuizCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Quiz duplicated",
                manageQuizLifecycleUseCase.duplicate(organizationId, quizId, command)));
    }

    @PostMapping("/{quizId}/questions/{questionId}/options")
    public ResponseEntity<ApiResponse<QuizResponse>> addAnswerOption(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @Valid @RequestBody AnswerOptionCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Answer option added",
                editQuizContentUseCase.addAnswerOption(organizationId, quizId, questionId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}/options/{optionId}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateAnswerOption(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @PathVariable UUID optionId,
            @Valid @RequestBody AnswerOptionCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Answer option updated",
                editQuizContentUseCase.updateAnswerOption(organizationId, quizId, questionId, optionId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}/options/order")
    public ResponseEntity<ApiResponse<QuizResponse>> reorderAnswerOptions(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @Valid @RequestBody ReorderAnswerOptionsCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Answer options reordered",
                editQuizContentUseCase.reorderAnswerOptions(organizationId, quizId, questionId, command)));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}/options/{optionId}")
    public ResponseEntity<Void> removeAnswerOption(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @PathVariable UUID optionId) {
        editQuizContentUseCase.removeAnswerOption(organizationId, quizId, questionId, optionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions/{questionId}/assets")
    public ResponseEntity<ApiResponse<QuizResponse>> addAsset(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @Valid @RequestBody QuestionAssetCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED,
                "Question asset added",
                editQuizContentUseCase.addAsset(organizationId, quizId, questionId, command)));
    }

    @PutMapping("/{quizId}/questions/{questionId}/assets/{assetId}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateAsset(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @PathVariable UUID assetId,
            @Valid @RequestBody QuestionAssetCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Question asset updated",
                editQuizContentUseCase.updateAsset(organizationId, quizId, questionId, assetId, command)));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}/assets/{assetId}")
    public ResponseEntity<Void> removeAsset(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @PathVariable UUID assetId) {
        editQuizContentUseCase.removeAsset(organizationId, quizId, questionId, assetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{quizId}/questions/{questionId}/assets/images", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<QuizResponse>> uploadImage(
            @PathVariable UUID organizationId,
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String altText) throws java.io.IOException {
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
