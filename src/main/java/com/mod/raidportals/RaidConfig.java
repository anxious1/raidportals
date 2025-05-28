// src/main/java/com/mod/raidportals/RaidConfig.java
package com.mod.raidportals;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public class RaidConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    /** Общий (COMMON) раздел конфига */
    public static class Common {
        // Списки ID боссов по уровням
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> bossesLevel1;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> bossesLevel2;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> bossesLevel3;

        // Мин/макс монет
        public final ForgeConfigSpec.IntValue minCoins;
        public final ForgeConfigSpec.IntValue maxCoins;

        // Макс HP боссов по уровням
        public final ForgeConfigSpec.IntValue maxHpLevel1;
        public final ForgeConfigSpec.IntValue maxHpLevel2;
        public final ForgeConfigSpec.IntValue maxHpLevel3;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("raid_portals");

            bossesLevel1 = builder
                    .comment("Список ID боссов для уровня сложности 1 (тест: зомби)")
                    .defineList("bossesLevel1",
                            List.of("minecraft:zombie"),
                            o -> o instanceof String
                    );

            bossesLevel2 = builder
                    .comment("Список ID боссов для уровня сложности 2 (тест: зомби)")
                    .defineList("bossesLevel2",
                            List.of("minecraft:zombie"),
                            o -> o instanceof String
                    );

            bossesLevel3 = builder
                    .comment("Список ID боссов для уровня сложности 3 (тест: скелет)")
                    .defineList("bossesLevel3",
                            List.of("minecraft:skeleton"),
                            o -> o instanceof String
                    );

            builder.comment("Минимальное и максимальное количество монет рейда");
            minCoins = builder.defineInRange("minCoins",  5, 0, Integer.MAX_VALUE);
            maxCoins = builder.defineInRange("maxCoins", 10, 0, Integer.MAX_VALUE);

            builder.comment("Максимальное HP боссов для каждого уровня (тестовые значения)");
            maxHpLevel1 = builder.defineInRange("maxHpLevel1", 20, 1, Integer.MAX_VALUE);
            maxHpLevel2 = builder.defineInRange("maxHpLevel2", 20, 1, Integer.MAX_VALUE);
            maxHpLevel3 = builder.defineInRange("maxHpLevel3", 30, 1, Integer.MAX_VALUE);

            builder.pop();
        }
    }
}
