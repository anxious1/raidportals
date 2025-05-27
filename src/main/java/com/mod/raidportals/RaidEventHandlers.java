package com.mod.raidportals;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.level.LevelEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = RaidPortalsMod.MODID)
public class RaidEventHandlers {

    // Счётчик тиков — мы спавним портал каждые 1200 тиков (~1 минута)
    private static int tickCounter = 0;

    /** Сбрасываем счётчик при загрузке мира (только в Оверворлде) */
    @SubscribeEvent
    public static void onWorldLoad(Load event) {
        if (event.getLevel().isClientSide()) return;
        ServerLevel lvl = (ServerLevel) event.getLevel();
        if (lvl.dimension() == Level.OVERWORLD) {
            tickCounter = 0;
            RaidPortalsMod.LOGGER.info("[RaidEvent] World loaded, tickCounter reset");
        }
    }

    /**
     * Раз в 1200 тиков пробуем заспавнить портал для каждого игрока
     * — **только** если мы в Оверворлде.
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        // работаем только в END-фазе и только на сервере
        if (event.phase != Phase.END) return;
        if (!(event.level instanceof ServerLevel serverLevel)) return;

        // убеждаемся, что это Overworld
        if (serverLevel.dimension() != Level.OVERWORLD) return;

        // Инкремент и сброс счётчика
        tickCounter++;
        if (tickCounter < 1200) return;
        tickCounter = 0;

        // Берём всех онлайн-игроков именно в Overworld
        List<ServerPlayer> players = serverLevel.players();
        RaidPortalsMod.LOGGER.info("[RaidEvent] Spawning portals for {} player(s)", players.size());

        // Для каждого пытаемся заспавнить портал рядом с ним
        for (ServerPlayer player : players) {
            BlockPos center = player.blockPosition();
            int tier = serverLevel.random.nextInt(3) + 1;
            boolean success = RaidManager.spawnRaidPortal(serverLevel, tier, center);
            if (!success) {
                RaidPortalsMod.LOGGER.warn(
                        "[RaidEvent] Failed to spawn Tier{} portal around {}",
                        tier, center
                );
            }
        }
    }

    /**
     * Игрок выходит из измерения — пока ничего не делаем,
     * потому что закрытие входного портала осуществляется в RaidManager
     * когда ВСЕ участники пройдут через Exit-портал.
     */
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        // можно добавить лог, но не обязательно
    }

    /**
     * При смерти босса в арене спавним выходной портал.
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity().level() instanceof ServerLevel arena)) return;
        if (arena.dimension() != ModRegistry.RAID_ARENA) return;

        BlockPos deathPos = event.getEntity().blockPosition();
        RaidPortalsMod.LOGGER.info(
                "[RaidEvent] Boss died at {}, spawning exit portal", deathPos
        );
        RaidManager.spawnExitPortal(arena, deathPos);
    }
}
