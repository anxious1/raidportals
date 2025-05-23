package com.mod.raidportals;

import net.minecraftforge.common.ForgeConfigSpec;

public class RaidConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        // тут будут параметры
        COMMON_SPEC = builder.build();
    }
}
