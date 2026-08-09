package kahoot.clabs.kahoot_clabs.organization.application.dto;

import java.util.List;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.application.readmodel.OrganizationReadModel;
import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String logo,
        String timezone,
        String language,
        String status,
        List<OrganizationMemberResponse> members
) {

    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName().value(),
                organization.getSlug().value(),
                organization.getDescription(),
                organization.getLogo(),
                organization.getTimezone(),
                organization.getLanguage(),
                organization.getStatus().name(),
                organization.getMembers().stream()
                        .map(OrganizationMemberResponse::from)
                        .toList());
    }

    public static OrganizationResponse from(OrganizationReadModel readModel) {
        return new OrganizationResponse(
                readModel.id(),
                readModel.name(),
                readModel.slug(),
                readModel.description(),
                readModel.logo(),
                readModel.timezone(),
                readModel.language(),
                readModel.status(),
                readModel.members().stream()
                        .map(member -> new OrganizationMemberResponse(
                                member.id(),
                                member.userId(),
                                member.roleId(),
                                member.status(),
                                member.joinedAt()))
                        .toList());
    }
}
