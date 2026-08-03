package kahoot.clabs.kahoot_clabs.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.identity.application.port.AssetsStoragePort;
import kahoot.clabs.kahoot_clabs.organization.application.command.CreateOrganizationCommand;
import kahoot.clabs.kahoot_clabs.organization.application.dto.OrganizationResponse;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;
import kahoot.clabs.kahoot_clabs.organization.domain.exception.OrganizationSlugAlreadyTakenException;
import kahoot.clabs.kahoot_clabs.organization.domain.repository.OrganizationRepository;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationSlug;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class CreateOrganizationUseCase {

    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    private final OrganizationRepository organizationRepository;
    private final AssetsStoragePort assetsStoragePort;

    public CreateOrganizationUseCase(
            OrganizationRepository organizationRepository,
            AssetsStoragePort assetsStoragePort){
        this.organizationRepository = organizationRepository;
        this.assetsStoragePort = assetsStoragePort;
    }

    @Transactional
    public OrganizationResponse execute(
            CreateOrganizationCommand command,
            byte[] logo,
            String contentType,
            String filename) {

        String slug = OrganizationSlug.of(command.slug()).value();
        if (organizationRepository.existsBySlug(slug)) {
            throw new OrganizationSlugAlreadyTakenException(slug);
        }

        Organization organization = Organization.create(command.name(), slug);
        if (command.description() != null && !command.description().isBlank()) {
            organization.updateDetails(command.name(), command.description().trim());
        }

        if (logo != null && logo.length > 0) {
            validateImage(logo, contentType);
            String key = "organizations/%s/logo/%s%s".formatted(
                    slug,
                    UUID.randomUUID(),
                    extension(filename, contentType));
            String logoUrl = assetsStoragePort.upload(key, logo, contentType);
            organization.changeLogo(logoUrl);
        }

        return OrganizationResponse.from(organizationRepository.save(organization));
    }

    private void validateImage(byte[] content, String contentType) {
        if (content.length > MAX_IMAGE_SIZE_BYTES) {
            throw new DomainException("Logo must be at most 5 MB");
        }
        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType)
                && !"image/gif".equals(contentType)) {
            throw new DomainException("Only JPEG, PNG, WebP, and GIF images are allowed");
        }
    }

    private String extension(String filename, String contentType) {
        if (filename != null && filename.lastIndexOf('.') >= 0) {
            return filename.substring(filename.lastIndexOf('.')).toLowerCase();
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".gif";
        };
    }
}
