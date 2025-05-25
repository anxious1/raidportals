package com.mod.raidportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RaidPortalsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RaidEventHandlers {
    /**
     * Координаты, в которые телепортируется игрок в новом измерении.
     */
    private static final double ARENA_X = 0.5;
    private static final double ARENA_Y = 80.0;
    private static final double ARENA_Z = 0.5;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        // Сработает только на сервере
        if (event.player.level().isClientSide) return;

        Player player = event.player;
        // Сдвинем границы вокруг игрока, чтобы попадание хоть чуть-чуть пересекало портал
        AABB box = player.getBoundingBox().inflate(0.2, 0.1, 0.2);

        // Проверим, есть ли в этой области любой из наших портальных блоков
        boolean inside = player.level().getBlockStates(box)
                .anyMatch(state -> isRaidPortal(state.getBlock()));

        if (inside) {
            teleportToArena(player);
        }
    }

    private static boolean isRaidPortal(Block block) {
        return block == ModRegistry.RAID_PORTAL_LVL1.get()
                || block == ModRegistry.RAID_PORTAL_LVL2.get()
                || block == ModRegistry.RAID_PORTAL_LVL3.get();
    }

    private static void teleportToArena(Player rawPlayer) {
        if (!(rawPlayer instanceof ServerPlayer serverPlayer)) return;

        // Получаем целевой мир по ключу из ModRegistry
        ResourceKey<Level> dimKey = ModRegistry.RAID_ARENA;
        ServerLevel target = serverPlayer.getServer().getLevel(dimKey);
        if (target == null) return;

        // Телепортируем игрока в центр арены
        serverPlayer.teleportTo(
                target,
                ARENA_X, ARENA_Y, ARENA_Z,
                serverPlayer.getYRot(), serverPlayer.getXRot()
        );
    }
}
