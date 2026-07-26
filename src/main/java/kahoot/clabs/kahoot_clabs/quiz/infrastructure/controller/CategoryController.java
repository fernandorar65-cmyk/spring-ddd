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

import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.quiz.application.command.CreateCategoryCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.command.UpdateCategoryCommand;
import kahoot.clabs.kahoot_clabs.quiz.application.dto.CategoryResponse;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.CreateCategoryUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.DeleteCategoryUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.GetCategoryUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.ListCategoriesUseCase;
import kahoot.clabs.kahoot_clabs.quiz.application.usecase.UpdateCategoryUseCase;

@RestController
@RequestMapping("/api/v1/categories")
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
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCategoryUseCase.execute(command));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(getCategoryUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listByOrganization(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(listCategoriesUseCase.execute(organizationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryCommand command) {
        return ResponseEntity.ok(updateCategoryUseCase.execute(id, command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCategoryUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
