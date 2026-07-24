package kahoot.clabs.kahoot_clabs.users.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.users.application.dto.AuthUserResponse;
import kahoot.clabs.kahoot_clabs.users.application.dto.RegisterUserRequest;
import kahoot.clabs.kahoot_clabs.users.application.port.PasswordHasher;
import kahoot.clabs.kahoot_clabs.users.domain.enums.RoleType;
import kahoot.clabs.kahoot_clabs.users.domain.model.Organization;
import kahoot.clabs.kahoot_clabs.users.domain.model.User;
import kahoot.clabs.kahoot_clabs.users.domain.model.valueobject.Password;
import kahoot.clabs.kahoot_clabs.users.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.users.domain.repository.RoleRepository;
import kahoot.clabs.kahoot_clabs.users.domain.repository.UserRepository;

@Service
public class RegisterUserUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AuthUserResponse execute(RegisterUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DomainException("Email is already registered");
        }
        if (organizationRepository.findBySlug(request.organizationSlug()).isPresent()) {
            throw new DomainException("Organization slug is already taken");
        }

        Password.assertValidRaw(request.password());
        var adminRole = roleRepository.findByType(RoleType.ADMIN)
                .orElseThrow(() -> new DomainException("ADMIN role is not seeded"));

        Organization organization = organizationRepository.save(
                Organization.create(request.organizationName(), request.organizationSlug()));

        Password hashedPassword = Password.fromHashed(passwordHasher.hash(request.password()));
        User user = User.create(
                organization.getId(),
                request.email(),
                request.firstName(),
                request.lastName(),
                hashedPassword);
        user.changeRole(adminRole.getId());
        user = userRepository.save(user);

        return new AuthUserResponse(
                user.getId(),
                user.getOrganizationId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName());
    }
}
