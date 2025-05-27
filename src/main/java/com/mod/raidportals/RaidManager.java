package com.mod.raidportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RaidManager {
    private static BlockPos portalOrigin;
    private static Vec3i portalSize;
    private static ServerLevel overworldLevel;
    private static boolean hasActivePortal = false;
    private static boolean isRaidActive = false;
    private static BlockPos exitPortalOrigin;

    // Участники рейда и прошедшие Exit
    private static final Set<UUID> raidParticipants = new HashSet<>();
    private static final Set<UUID> exitParticipants = new HashSet<>();

    public static final Logger LOGGER = RaidPortalsMod.LOGGER;

    // Старт рейда: сохраняем координаты, размер, очищаем списки
    public static void startRaid(ServerLevel world, BlockPos origin, Vec3i size) {
        portalOrigin    = origin;
        portalSize      = size;
        overworldLevel  = world;
        hasActivePortal = true;
        isRaidActive    = true;
        raidParticipants.clear();
        exitParticipants.clear();
        LOGGER.info("[RaidManager] Raid started at {} size {}", origin, size);
    }

    // Добавляем участника, когда он входит в портал
    public static void addParticipant(Player p) {
        if (!hasActivePortal) return;
        raidParticipants.add(p.getUUID());
        LOGGER.info("[RaidManager] Participant joined: {} (total={})",
                p.getName().getString(), raidParticipants.size());
    }

    // Спавним входной портал (вызывается из события)
    public static boolean spawnRaidPortal(ServerLevel world, int tier, BlockPos center) {
        final int RADIUS = 500, ATTEMPTS = 200;
        StructureTemplate tpl = world.getStructureManager()
                .get(new net.minecraft.resources.ResourceLocation(RaidPortalsMod.MODID, "portal_tir_" + tier))
                .orElse(null);
        if (tpl == null) {
            LOGGER.error("[RaidManager] Template tier{} not found", tier);
            return false;
        }
        Vec3i size = tpl.getSize();
        for (int i = 0; i < ATTEMPTS; i++) {
            int x = center.getX() + world.random.nextInt(RADIUS*2) - RADIUS;
            int z = center.getZ() + world.random.nextInt(RADIUS*2) - RADIUS;
            int y = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            if (y <= world.getMinBuildHeight()) continue;
            BlockPos place = new BlockPos(x - size.getX()/2, y, z - size.getZ()/2);
            tpl.placeInWorld(world, place, place, new StructurePlaceSettings(), world.random, 2);
            startRaid(world, place, size);
            LOGGER.info("[RaidManager] SUCCESS Tier{} at {}", tier, place);
            return true;
        }
        LOGGER.warn("[RaidManager] FAILED Tier{} near {}", tier, center);
        return false;
    }

    // Спавним Exit-портал чуть над точкой bossPos
    public static void spawnExitPortal(ServerLevel arena, BlockPos bossPos) {
        StructureTemplate tpl = arena.getStructureManager()
                .get(new net.minecraft.resources.ResourceLocation(RaidPortalsMod.MODID, "exit_portal"))
                .orElse(null);
        if (tpl == null) {
            LOGGER.error("[RaidManager] exit_portal template not found");
            return;
        }
        Vec3i size = tpl.getSize();
        exitPortalOrigin = bossPos.above().offset(-size.getX()/2, 0, -size.getZ()/2);
        tpl.placeInWorld(arena, exitPortalOrigin, exitPortalOrigin,
                new StructurePlaceSettings(), arena.random, 2);
        LOGGER.info("[RaidManager] Exit portal spawned at {}", exitPortalOrigin);
    }

    // Когда игрок заходит в Exit-портал
    public static void onPlayerExit(Player p) {
        if (!isRaidActive) return;
        exitParticipants.add(p.getUUID());
        LOGGER.info("[RaidManager] Exit crossed by {} ({}/{})",
                p.getName().getString(), exitParticipants.size(), raidParticipants.size());
        if (exitParticipants.containsAll(raidParticipants)) {
            // все участники вышли — закрываем оба портала
            if (overworldLevel != null) closeRaidPortal(overworldLevel);
            // closeExit в соответствующем измерении делается в блоке
            isRaidActive = false;
            LOGGER.info("[RaidManager] Raid fully closed");
        }
    }

    // Удаляем входной портал
    public static void closeRaidPortal(ServerLevel world) {
        if (!hasActivePortal || portalOrigin == null || portalSize == null) return;
        for (int dx = 0; dx < portalSize.getX(); dx++)
            for (int dy = 0; dy < portalSize.getY(); dy++)
                for (int dz = 0; dz < portalSize.getZ(); dz++)
                    world.setBlock(portalOrigin.offset(dx,dy,dz),
                            Blocks.AIR.defaultBlockState(), 3);
        hasActivePortal = false;
        LOGGER.info("[RaidManager] Input portal removed at {}", portalOrigin);
    }

    // Удаляем Exit-портал
    public static void closeExitPortal(ServerLevel arena) {
        if (exitPortalOrigin == null) return;
        arena.setBlock(exitPortalOrigin, Blocks.AIR.defaultBlockState(), 3);
        LOGGER.info("[RaidManager] Exit portal removed at {}", exitPortalOrigin);
        exitPortalOrigin = null;
    }

    // Геттеры для ExitPortalBlock
    public static ServerLevel getOverworldLevel() { return overworldLevel; }
    public static BlockPos getActivePortalPos()     { return portalOrigin; }

    // --- НОВЫЕ МЕТОДЫ ДЛЯ Tier1PortalBlock ---
    /** Сохраняет позицию входного портала и уровень, в котором он стоит. */
    public static void setActivePortalPosAndLevel(ServerLevel level, BlockPos origin) {
        portalOrigin    = origin;
        overworldLevel  = level;
        hasActivePortal = true;
        LOGGER.info("[RaidManager] Active portal set at {} in {}", origin, level.dimension());
    }

    /** Включает или выключает флаг "есть активный портал". */
    public static void setActivePortal(boolean active) {
        hasActivePortal = active;
        LOGGER.info("[RaidManager] hasActivePortal = {}", active);
    }
}
