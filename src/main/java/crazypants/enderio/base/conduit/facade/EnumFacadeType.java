package crazypants.enderio.base.conduit.facade;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

import com.enderio.core.common.util.NullHelper;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum EnumFacadeType implements IStringSerializable {

    BASIC("", false, false),
    HARDENED(".hardened", true, false),
    TRANSPARENT(".transparent", false, true),
    TRANSPARENT_HARDENED(".transparent.hardened", true, true);

    @NotNull
    private final String namePostfix;
    @Getter
    private final boolean hardened;
    @Getter
    private final boolean transparent;

    @NotNull
    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @NotNull
    public String getUnlocalizedName(@NotNull Item item) {
        return item.getTranslationKey() + namePostfix;
    }

    @NotNull
    public static EnumFacadeType getTypeFromMeta(int meta) {
        return NullHelper.first(values()[MathHelper.clamp(meta, 0, values().length - 1)], BASIC);
    }

    public static int getMetaFromType(@NotNull EnumFacadeType value) {
        return value.ordinal();
    }

}
