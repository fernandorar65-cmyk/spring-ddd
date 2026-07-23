package kahoot.clabs.kahoot_clabs.quizes.domain.Enums;
public enum MediaType {

    IMAGE("Imagen"),
    VIDEO("Video"),
    AUDIO("Audio"),
    GIF("GIF"),
    DOCUMENT("Documento");

    private final String displayName;

    MediaType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}