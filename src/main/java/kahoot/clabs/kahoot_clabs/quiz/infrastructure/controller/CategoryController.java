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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.quiz.application.command.CreateCategoryCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.UpdateCategoryCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.CreateCategoryUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.DeleteCategoryUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.GetCategoryUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ListCategoriesUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.UpdateCategoryUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

// cateogiras tanto para quizes como para preguntas     para las organizaciones tendremos sectores
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Categorías de quizzes por organización")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
            CreateCategoryUseCase createCategoryUseCase,
            GetCategoryUseCase getCategoryUseCase,
            ListCategoriesUseCase listCategoriesUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una categoría asociada a una organización para clasificar quizzes.")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Category created", createCategoryUseCase.execute(command)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría", description = "Devuelve una categoría por su identificador.")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            @Parameter(description = "Identificador de la categoría") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Category retrieved", getCategoryUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Lista todas las categorías de una organización.")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listByOrganization(
            @Parameter(description = "Identificador de la organización") @RequestParam UUID organizationId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Categories retrieved", listCategoriesUseCase.execute(organizationId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza el nombre o descripción de una categoría.")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @Parameter(description = "Identificador de la categoría") @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryCommand command) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK, "Category updated", updateCategoryUseCase.execute(id, command)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría. Debe validarse que no rompa reglas de negocio asociadas.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador de la categoría") @PathVariable UUID id) {
        deleteCategoryUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
