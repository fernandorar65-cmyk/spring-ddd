package kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

public record AnswerOptionSnapshot(UUID originalOptionId, String text, boolean correct, int orderIndex) {

    public AnswerOptionSnapshot {
        if (originalOptionId == null) {
            throw new DomainException("Original answer option id is required");
        }
        if (text == null || text.isBlank()) {
            throw new DomainException("Answer option text is required");
        }
        if (orderIndex < 1) {
            throw new DomainException("Answer option order index must be at least 1");
        }
    }
}
