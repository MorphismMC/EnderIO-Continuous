package crazypants.enderio.base.recipe;

public enum RecipeBonusType {

    NONE(false, false),
    MULTIPLY_OUTPUT(true, true),
    CHANCE_ONLY(false, true);

    private final boolean multiply, chances;

    public static RecipeBonusType fromString(String s) {
        return switch (s.toLowerCase()) {
            case "multiply_output" -> MULTIPLY_OUTPUT;
            case "chance_only" -> CHANCE_ONLY;
            default -> NONE;
        };
    }

    RecipeBonusType(boolean multiply, boolean chances) {
        this.multiply = multiply;
        this.chances = chances;
    }

    public boolean doMultiply() {
        return multiply;
    }

    public boolean doChances() {
        return chances;
    }

    public boolean useBalls() {
        return multiply || chances;
    }
}
