package crazypants.enderio.conduits.conduit;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ItemConduitSubtype {

    private final String unlocalisedName;
    private final String modelLocation;

    public ItemConduitSubtype(@NotNull String baseName, @NotNull String iconKey) {
        this.unlocalisedName = "enderio." + baseName;
        this.modelLocation = iconKey;
    }

}
