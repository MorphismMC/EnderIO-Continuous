package crazypants.enderio.base.conduit;

import static crazypants.enderio.base.gui.IconEIO.CROSS;
import static crazypants.enderio.base.gui.IconEIO.TICK;
import static crazypants.enderio.base.gui.IconEIO.YETA_GEAR;
import static crazypants.enderio.util.NbtValue.DISPLAYMODE;

import lombok.Getter;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import crazypants.enderio.api.tool.ConduitControllable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConduitDisplayMode {

    @NotNull
    public static final ConduitDisplayMode NEUTRAL = new ConduitDisplayMode("neutral", YETA_GEAR, YETA_GEAR);
    @NotNull
    public static final ConduitDisplayMode ALL = new ConduitDisplayMode("all", TICK, TICK);
    @NotNull
    public static final ConduitDisplayMode NONE = new ConduitDisplayMode("none", CROSS, CROSS);

    @NotNull
    private static final NNList<ConduitDisplayMode> registrar = new NNList<>();

    @Getter
    @NotNull
    private final Class<? extends Conduit> conduitType;
    @Getter
    @NotNull
    private final IWidgetIcon widgetSelected;
    @Getter
    @NotNull
    private final IWidgetIcon widgetUnselected;

    @Nullable
    private String overrideName = null;

    static {
        registerDisplayMode(NEUTRAL);
        registerDisplayMode(NONE);
        registerDisplayMode(ALL);
    }

    /**
     * Use this constructor if you have custom display logic, it will use {@link Conduit} as the {@code conduitType}, and
     * the passed name as the override name.
     *
     * @param name             The override name.
     * @param widgetSelected   The widget to render when this type is selected.
     * @param widgetUnselected The widget to render when this type is unselected.
     *
     * @see #ConduitDisplayMode(Class, IWidgetIcon, IWidgetIcon)
     */
    public ConduitDisplayMode(@NotNull String name,
                              @NotNull IWidgetIcon widgetSelected,
                              @NotNull IWidgetIcon widgetUnselected) {
        this(Conduit.class, widgetSelected, widgetUnselected);
        setName(name);
    }

    /**
     * Creates a new display mode for any {@link ConduitControllable} wrench.
     * <p>
     * Contains data about which conduit type this is for, and the icons to render while holding the wrench.
     *
     * @param conduitType      The base class for your conduit type, typically an interface (e.g. {@code PowerConduit}).
     * @param widgetSelected   The widget to render when this type is selected.
     * @param widgetUnselected The widget to render when this type is unselected.
     */
    public ConduitDisplayMode(@NotNull Class<? extends Conduit> conduitType,
                              @NotNull IWidgetIcon widgetSelected,
                              @NotNull IWidgetIcon widgetUnselected) {
        this.conduitType = conduitType;
        this.widgetSelected = widgetSelected;
        this.widgetUnselected = widgetUnselected;
    }

    public static void registerDisplayMode(@NotNull ConduitDisplayMode mode) {
        if (!registrar.contains(mode)) {
            registrar.add(mode);
        }
    }

    public boolean renderConduit(@Nullable Class<? extends Conduit> conduitTypeIn) {
        if (this == ALL || this == NEUTRAL || conduitTypeIn == null) {
            return true;
        } else if (this == NONE) {
            return false;
        } else {
            return this.conduitType.isAssignableFrom(conduitTypeIn);
        }
    }

    @NotNull
    public String getName() {
        return overrideName == null ? conduitType.getSimpleName() : overrideName;
    }

    /**
     * The name is null by default, and will use the simple class name of the conduit type.
     * 
     * @param name The override name to set.
     */
    public void setName(@NotNull String name) {
        this.overrideName = name;
    }

    @NotNull
    public static ConduitDisplayMode next(@NotNull ConduitDisplayMode mode) {
        return registrar.next(mode);
    }

    @NotNull
    public static ConduitDisplayMode previous(@NotNull ConduitDisplayMode mode) {
        return registrar.prev(mode);
    }

    @Nullable
    public static ConduitDisplayMode fromName(String name) {
        for (ConduitDisplayMode mode : registrar) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return null;
    }

    public int ordinal() {
        return registrar.indexOf(this);
    }

    @NotNull
    public static ConduitDisplayMode getDisplayMode(@NotNull ItemStack equippedStack) {
        if (!(equippedStack.getItem() instanceof ConduitControllable)) {
            return ALL;
        }
        ConduitDisplayMode mode = fromName(DISPLAYMODE.getString(equippedStack, ConduitDisplayMode.ALL.getName()));
        if (mode == null) { // Backwards compat.
            setDisplayMode(equippedStack, ALL);
            return ALL;
        }
        return mode;
    }

    public static void setDisplayMode(@NotNull ItemStack equippedStack, @NotNull ConduitDisplayMode mode) {
        if (!(equippedStack.getItem() instanceof ConduitControllable)) {
            return;
        }
        DISPLAYMODE.setString(equippedStack, mode.getName());
    }

    @NotNull
    public ConduitDisplayMode next() {
        return next(this);
    }

    @NotNull
    public ConduitDisplayMode previous() {
        return previous(this);
    }

    public boolean isAll() {
        return this == ALL || this == NEUTRAL;
    }

    public static int registrySize() {
        return registrar.size() - 2;
    }

    public static Iterable<ConduitDisplayMode> getRenderableModes() {
        return FluentIterable.from(registrar).filter(new Predicate<>() {

            @Override
            public boolean apply(@Nullable ConduitDisplayMode input) {
                return input != ALL && input != NONE; // && input != NEUTRAL;
            }

            @Override
            public boolean equals(@Nullable Object o) {
                return super.equals(o);
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }

        });
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        ConduitDisplayMode other = (ConduitDisplayMode) o;
        if (!conduitType.equals(other.conduitType))
            return false;
        return Objects.equal(overrideName, other.overrideName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + conduitType.hashCode();
        result = prime * result + NullHelper.first(overrideName, "").hashCode();
        return result;
    }

}
