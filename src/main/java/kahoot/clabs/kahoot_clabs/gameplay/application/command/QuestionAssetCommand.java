package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.MediaType;

public record QuestionAssetCommand(
        @NotNull MediaType type,
        @NotBlank @Size(max = 1000) String url,
        @Size(max = 1000) String thumbnailUrl,
        @Size(max = 255) String altText,
        @Positive Integer durationSeconds) {
}
