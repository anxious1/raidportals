package com.mod.raidportals;

import com.mod.raidportals.advancement.BossKilledTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(RaidPortalsMod.MODID)
public class RaidPortalsMod {
    public static final String MODID = "raidportals";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RaidPortalsMod() {
        // 1) Регистрация блоков и предметов
        ModRegistry.init(FMLJavaModLoadingContext.get().getModEventBus());

        // 2) Регистрация COMMON-конфига
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, RaidConfig.COMMON_SPEC);

        // 3) Подписка на игровые события
        MinecraftForge.EVENT_BUS.register(RaidEventHandlers.class);

        // 4) Клиентская и общая загрузка
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onCommonSetup);

        LOGGER.info("RaidPortalsMod initialized");
    }

    /** Общие настройки: логируем конфиг и регистрируем триггер */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        // Регистрируем свой Criterion-триггер
        CriteriaTriggers.register(BossKilledTrigger.INSTANCE);
        LOGGER.info("Registered BossKilledTrigger");

        LOGGER.info("=== RaidPortals CONFIG ===");
        LOGGER.info("Level1 bosses = {}", RaidConfig.COMMON.bossesLevel1.get());
        LOGGER.info("Level2 bosses = {}", RaidConfig.COMMON.bossesLevel2.get());
        LOGGER.info("Level3 bosses = {}", RaidConfig.COMMON.bossesLevel3.get());
        LOGGER.info("Coins range = {}..{}",
                RaidConfig.COMMON.minCoins.get(),
                RaidConfig.COMMON.maxCoins.get()
        );
        LOGGER.info("Max HP = L1:{} L2:{} L3:{}",
                RaidConfig.COMMON.maxHpLevel1.get(),
                RaidConfig.COMMON.maxHpLevel2.get(),
                RaidConfig.COMMON.maxHpLevel3.get()
        );
    }
}
