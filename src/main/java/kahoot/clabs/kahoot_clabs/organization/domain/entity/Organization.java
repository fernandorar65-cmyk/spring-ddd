package kahoot.clabs.kahoot_clabs.organization.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationName;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationSlug;
import kahoot.clabs.kahoot_clabs.organization.domain.valueobject.OrganizationStatus;
import kahoot.clabs.kahoot_clabs.shared.domain.AuditableEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Organization as domain entity, aligned with {@code organizations} persistence fields.
 * Prefer the aggregate root in {@code domain.aggregate.Organization} when member lifecycle
 * must be coordinated inside the same consistency boundary.
 */
public class Organization extends AuditableEntity {

    private static final String DEFAULT_TIMEZONE = "America/Bogota";
    private static final String DEFAULT_LANGUAGE = "es";

    private OrganizationName name;
    private OrganizationSlug slug;
    private String logoUrl;
    private String description;
    private String timezone;
    private String language;
    private OrganizationStatus status;

    private Organization(
            UUID id,
            OrganizationName name,
            OrganizationSlug slug,
            String logoUrl,
            String description,
            String timezone,
            String language,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.logoUrl = logoUrl;
        this.description = description;
        this.timezone = (timezone == null || timezone.isBlank()) ? DEFAULT_TIMEZONE : timezone.trim();
        this.language = (language == null || language.isBlank()) ? DEFAULT_LANGUAGE : language.trim();
        this.status = status != null ? status : OrganizationStatus.ACTIVE;
    }

    public static Organization create(String name, String slug) {
        return new Organization(
                null,
                OrganizationName.of(name),
                OrganizationSlug.of(slug),
                null,
                null,
                DEFAULT_TIMEZONE,
                DEFAULT_LANGUAGE,
                OrganizationStatus.ACTIVE,
                null,
                null);
    }

    public static Organization rehydrate(
            UUID id,
            String name,
            String slug,
            String logoUrl,
            String description,
            String timezone,
            String language,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Organization(
                id,
                OrganizationName.of(name),
                OrganizationSlug.of(slug),
                logoUrl,
                description,
                timezone,
                language,
                status,
                createdAt,
                updatedAt);
    }

    public void updateDetails(String name, String description) {
        this.name = OrganizationName.of(name);
        this.description = description;
        touch();
    }

    public void changeSlug(String slug) {
        this.slug = OrganizationSlug.of(slug);
        touch();
    }

    public void changeLogo(String logoUrl) {
        this.logoUrl = logoUrl;
        touch();
    }

    public void changeLocalization(String timezone, String language) {
        if (timezone != null && !timezone.isBlank()) {
            this.timezone = timezone.trim();
        }
        if (language != null && !language.isBlank()) {
            this.language = language.trim();
        }
        touch();
    }

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        this.status = OrganizationStatus.INACTIVE;
        touch();
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
        touch();
    }

    public void ensureCanAcceptMembers() {
        if (status == OrganizationStatus.SUSPENDED) {
            throw new DomainException("A suspended organization cannot accept members");
        }
    }

    public OrganizationName getName() {
        return name;
    }

    public OrganizationSlug getSlug() {
        return slug;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLanguage() {
        return language;
    }

    public OrganizationStatus getStatus() {
        return status;
    }
}
