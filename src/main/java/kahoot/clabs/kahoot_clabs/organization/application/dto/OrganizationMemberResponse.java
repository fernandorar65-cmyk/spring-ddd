package kahoot.clabs.kahoot_clabs.organization.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.organization.domain.entity.OrganizationMember;

public record OrganizationMemberResponse(
        UUID id,
        UUID userId,
        UUID roleId,
        String status,
        LocalDateTime joinedAt
) {

    public static OrganizationMemberResponse from(OrganizationMember member) {
        return new OrganizationMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getRoleId(),
                member.getStatus().name(),
                member.getJoinedAt());
    }
}
