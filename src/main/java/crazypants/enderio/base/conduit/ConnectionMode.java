package crazypants.enderio.base.conduit;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public enum ConnectionMode {

    IN_OUT("gui.conduit.io_mode.in_out"),
    INPUT("gui.conduit.io_mode.input"),
    OUTPUT("gui.conduit.io_mode.output"),
    DISABLED("gui.conduit.io_mode.disabled"),
    NOT_SET("gui.conduit.io_mode.not_set");

    @NotNull
    private final String unlocalisedName;

    @NotNull
    public static ConnectionMode getNext(@NotNull ConnectionMode mode) {
        int ord = mode.ordinal() + 1;
        if (ord >= ConnectionMode.values().length) {
            ord = 0;
        }
        return NullHelper.first(ConnectionMode.values()[ord], NOT_SET);
    }

    @NotNull
    public static ConnectionMode getPrevious(@NotNull ConnectionMode mode) {
        int ord = mode.ordinal() - 1;
        if (ord < 0) {
            ord = ConnectionMode.values().length - 1;
        }
        return NullHelper.first(ConnectionMode.values()[ord], NOT_SET);
    }

    public boolean acceptsInput() {
        return this == IN_OUT || this == INPUT;
    }

    public boolean acceptsOutput() {
        return this == IN_OUT || this == OUTPUT;
    }

    public boolean isActive() {
        return this != DISABLED;
    }

    @NotNull
    public String getLocalisedName() {
        return EnderIO.lang.localize(unlocalisedName);
    }

    static {
        for (ConnectionMode lang : values()) {
            if (!EnderIO.lang.canLocalize(lang.unlocalisedName)) {
                Log.error("Missing translation for '" + lang.unlocalisedName);
            }
        }
    }

}
