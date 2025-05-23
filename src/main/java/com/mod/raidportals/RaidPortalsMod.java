package com.mod.raidportals;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(RaidPortalsMod.MODID)
public class RaidPortalsMod {
    public static final String MODID = "raidportals";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RaidPortalsMod() {
        // Инициализация регистрации блоков и предметов
        ModRegistry.init(FMLJavaModLoadingContext.get().getModEventBus());

        // Регистрация общего конфига
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, RaidConfig.COMMON_SPEC);

        // Подписка на шину игровых событий (для RaidEventHandlers)
        MinecraftForge.EVENT_BUS.register(RaidEventHandlers.class);

        LOGGER.info("RaidPortalsMod initialized");
    }
}
