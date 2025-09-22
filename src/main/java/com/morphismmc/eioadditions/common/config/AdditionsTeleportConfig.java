package com.morphismmc.eioadditions.common.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class AdditionsTeleportConfig {

    public static final IValueFactory F = AdditionsConfig.F.section("teleport");

    public static final IValue<Integer> rangeTeleportStaff2Block = F
            .make("defaultTeleportRangeTeleportStaffToBlock", 2048, //
                    "Default range of travel using an item to a block (e.g. Staff to Travel Anchors).")
            .setRange(128, 4096)
            .sync();

    public static final IValue<Integer> rangeTeleportStaff2Blink = F
            .make("defaultTeleportRangeTeleportStaff", 64, //
                    "Default range of travel using an item (e.g. Staff blinking).")
            .setRange(16, 512)
            .sync();
}
