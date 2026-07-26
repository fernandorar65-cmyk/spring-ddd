package kahoot.clabs.kahoot_clabs.identity.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.identity.application.command.LoginCommand;
import kahoot.clabs.kahoot_clabs.identity.application.command.RegisterUserCommand;
import kahoot.clabs.kahoot_clabs.identity.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.LoginUserUseCase;
import kahoot.clabs.kahoot_clabs.identity.application.usecase.RegisterUserUseCase;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.web.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthUserResponse>> register(@Valid @RequestBody RegisterUserCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "User registered", registerUserUseCase.execute(command)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthUserResponse>> login(@Valid @RequestBody LoginCommand command) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Login successful", loginUserUseCase.execute(command)));
    }
}
