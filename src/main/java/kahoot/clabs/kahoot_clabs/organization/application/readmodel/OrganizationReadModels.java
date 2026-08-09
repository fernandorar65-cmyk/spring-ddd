package kahoot.clabs.kahoot_clabs.organization.application.readmodel;

import kahoot.clabs.kahoot_clabs.organization.domain.aggregate.Organization;

public final class OrganizationReadModels {

    private OrganizationReadModels() {
    }

    public static OrganizationReadModel from(Organization organization) {
        return new OrganizationReadModel(
                organization.getId(),
                organization.getName().value(),
                organization.getSlug().value(),
                organization.getDescription(),
                organization.getLogo(),
                organization.getTimezone(),
                organization.getLanguage(),
                organization.getStatus().name(),
                organization.getCreatedAt(),
                organization.getUpdatedAt(),
                organization.getMembers().stream()
                        .map(member -> new OrganizationReadModel.MemberReadModel(
                                member.getId(),
                                member.getUserId(),
                                member.getRoleId(),
                                member.getStatus().name(),
                                member.getJoinedAt()))
                        .toList());
    }
}
