package kahoot.clabs.kahoot_clabs.users.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kahoot.clabs.kahoot_clabs.users.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.users.application.dto.LoginRequest;
import kahoot.clabs.kahoot_clabs.users.application.dto.RegisterUserRequest;
import kahoot.clabs.kahoot_clabs.users.application.usecase.LoginUserUseCase;
import kahoot.clabs.kahoot_clabs.users.application.usecase.RegisterUserUseCase;

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
    public ResponseEntity<AuthUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUserUseCase.execute(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUserUseCase.execute(request));
    }
}
