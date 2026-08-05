package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record HostActionCommand(@NotNull UUID hostUserId) {
}
