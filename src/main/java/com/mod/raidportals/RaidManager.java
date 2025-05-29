package com.mod.raidportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RaidManager {
    // === ДАННЫЕ ===

    // Информация о входном портале
    private static PortalData entryPortal;

    // Куда возвращать игроков
    private static ServerLevel overworldLevel;
    private static BlockPos    overworldOrigin;

    // Данные по активным выходным порталам
    private static final Map<BlockPos, PortalData> exitPortals = new HashMap<>();

    // UUID участников рейда
    private static final Set<UUID> raidParticipants = new HashSet<>();

    // Состояние рейда
    private static boolean hasActivePortal = false;
    private static boolean bossSpawned     = false;
    private static int     currentTier     = 1;
    private static Vec3    lastTeleportPos;

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();
    public static final Logger LOGGER = RaidPortalsMod.LOGGER;

    private static class PortalData {
        final ServerLevel world;
        final BlockPos    origin;
        final Vec3i       size;
        final StructureTemplate tpl;
        final StructurePlaceSettings settings;
        final List<BlockPos> placedBlocks = new ArrayList<>();
        final Set<UUID>      exited       = new HashSet<>();

        PortalData(ServerLevel world, BlockPos origin, Vec3i size,
                   StructureTemplate tpl, StructurePlaceSettings settings) {
            this.world    = world;
            this.origin   = origin;
            this.size     = size;
            this.tpl      = tpl;
            this.settings = settings;
        }
    }

    // === МЕТОДЫ, используемые TierXPortalBlock и RaidEventHandlers ===

    /** Сохраняет уровень сложности (Tier). */
    public static void setCurrentTier(int tier) {
        currentTier = tier;
        LOGGER.info("[RaidManager] currentTier = {}", tier);
    }

    /** Запоминает точку спавна босса (последняя телепозиция). */
    public static void setLastTeleportPos(Vec3 pos) {
        lastTeleportPos = pos;
        LOGGER.info("[RaidManager] lastTeleportPos = {}", pos);
    }

    /** Фиксирует входной портал: где в оверворлд и с какого блока. */
    public static void setActivePortalPosAndLevel(ServerLevel level, BlockPos origin) {
        overworldLevel  = level;
        overworldOrigin = origin;
        hasActivePortal = true;
        LOGGER.info("[RaidManager] entry portal registered at {} in {}", origin, level.dimension());
    }

    /** Включает/выключает флаг активности входного портала. */
    public static void setActivePortal(boolean active) {
        hasActivePortal = active;
        LOGGER.info("[RaidManager] hasActivePortal = {}", active);
    }

    /** Проверяет, выходил ли игрок уже через этот exit-портал. */
    public static boolean hasExitedThrough(BlockPos pos, UUID playerId) {
        PortalData pd = exitPortals.get(pos);
        boolean res = pd != null && pd.exited.contains(playerId);
        LOGGER.info("[RaidManager] hasExitedThrough({}, {}) = {}", pos, playerId, res);
        return res;
    }

    /** Отдаёт уровень оверворлд для возврата игроков. */
    public static ServerLevel getOverworldLevel() {
        return overworldLevel;
    }

    /** Отдаёт точку входного портала в оверворлд. */
    public static BlockPos getEntryPortalPos() {
        return overworldOrigin;
    }

    /** Добавляет игрока в список участников рейда. */
    public static void addParticipant(Player player) {
        addParticipant(player.getUUID());
    }
    public static void addParticipant(UUID playerId) {
        if (!hasActivePortal) return;
        raidParticipants.add(playerId);
        LOGGER.info("[RaidManager] addParticipant {} (total {})",
                playerId, raidParticipants.size());
    }

    /** Текущий Tier (для логов и расчётов). */
    public static int getCurrentTier() {
        return currentTier;
    }

    // === Спавн и автo-удаление входного портала ===

    /**
     * (Опционально) спавнит входной портал по шаблону portal_tir_<tier>,
     * сохраняет все поставленные блоки, и если никто не вошёл за минуту —
     * удаляет его автоматически.
     */
    public static boolean spawnRaidPortal(ServerLevel world, int tier, BlockPos center) {
        if (hasActivePortal) return false;
        setCurrentTier(tier);

        StructureTemplate tpl = world.getStructureManager()
                .get(new ResourceLocation(RaidPortalsMod.MODID, "portal_tir_" + tier))
                .orElse(null);
        if (tpl == null) {
            LOGGER.error("[RaidManager] template Tier{} not found", tier);
            return false;
        }

        Vec3i size = tpl.getSize();
        StructurePlaceSettings settings = new StructurePlaceSettings();
        final int RADIUS = 500, ATTEMPTS = 200;

        for (int i = 0; i < ATTEMPTS; i++) {
            int x = center.getX() + world.random.nextInt(RADIUS * 2) - RADIUS;
            int z = center.getZ() + world.random.nextInt(RADIUS * 2) - RADIUS;
            int y = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            if (y <= world.getMinBuildHeight()) continue;
            BlockPos place = new BlockPos(x - size.getX()/2, y, z - size.getZ()/2);

            tpl.placeInWorld(world, place, place, settings, world.random, 2);

            // Сохраняем список реальных блоков
            PortalData pd = new PortalData(world, place, size, tpl, settings);
            for (int dx = 0; dx < size.getX(); dx++)
                for (int dy = 0; dy < size.getY(); dy++)
                    for (int dz = 0; dz < size.getZ(); dz++) {
                        BlockPos bp = place.offset(dx, dy, dz);
                        if (!world.getBlockState(bp).isAir()) pd.placedBlocks.add(bp);
                    }

            entryPortal     = pd;
            overworldLevel  = world;
            overworldOrigin = place;
            hasActivePortal = true;
            bossSpawned     = false;
            raidParticipants.clear();
            exitPortals.clear();

            // Таймер: через минуту удаляем, если никто не вошёл
            SCHEDULER.schedule(() -> {
                if (raidParticipants.isEmpty() && hasActivePortal) {
                    for (BlockPos bp : pd.placedBlocks) {
                        pd.world.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
                    }
                    hasActivePortal = false;
                    LOGGER.info("[RaidManager] auto-removed entry at {}", place);
                }
            }, 1, TimeUnit.MINUTES);

            LOGGER.info("[RaidManager] spawnRaidPortal Tier{} at {}", tier, place);
            return true;
        }

        LOGGER.warn("[RaidManager] failed to spawn Tier{} portal near {}", tier, center);
        return false;
    }

    // === Спавн выходного портала ===

    /** Спавнит exit-портал шаблоном exit_portal в bossPos.above(). */
    public static void spawnExitPortal(ServerLevel world, BlockPos bossPos) {
        StructureTemplate tpl = world.getStructureManager()
                .get(new ResourceLocation(RaidPortalsMod.MODID, "exit_portal"))
                .orElse(null);
        if (tpl == null) {
            LOGGER.error("[RaidManager] template exit_portal not found");
            return;
        }

        Vec3i size = tpl.getSize();
        StructurePlaceSettings settings = new StructurePlaceSettings();
        BlockPos exitPos = bossPos.above();

        tpl.placeInWorld(world, exitPos, exitPos, settings, world.random, 2);

        PortalData pd = new PortalData(world, exitPos, size, tpl, settings);
        for (int dx = 0; dx < size.getX(); dx++)
            for (int dy = 0; dy < size.getY(); dy++)
                for (int dz = 0; dz < size.getZ(); dz++) {
                    BlockPos bp = exitPos.offset(dx, dy, dz);
                    if (!world.getBlockState(bp).isAir()) pd.placedBlocks.add(bp);
                }
        exitPortals.put(exitPos, pd);

        LOGGER.info("[RaidManager] spawnExitPortal at {}", exitPos);
    }

    // === Обработка выхода игроков и удаление порталов ===

    public static void onPlayerExit(BlockPos origin, UUID playerId) {
        PortalData pd = exitPortals.get(origin);
        if (pd == null) {
            LOGGER.warn("[RaidManager] onPlayerExit: no exit portal at {}", origin);
            return;
        }

        pd.exited.add(playerId);
        LOGGER.info("[RaidManager] {} exited ({}/{})",
                playerId, pd.exited.size(), raidParticipants.size());
        if (!pd.exited.containsAll(raidParticipants)) return;

        // Телепорт последнего участника на оверворлд
        ServerPlayer sp = pd.world.getServer()
                .getPlayerList().getPlayer(playerId);
        if (sp != null && overworldLevel != null && overworldOrigin != null) {
            sp.teleportTo(
                    overworldLevel,
                    overworldOrigin.getX() + 0.5,
                    overworldOrigin.getY() + 1,
                    overworldOrigin.getZ() + 0.5,
                    sp.getYRot(),
                    sp.getXRot()
            );
            LOGGER.info("[RaidManager] teleported {} back to {}", playerId, overworldOrigin);
        }

        // Удаляем exit-портал
        for (BlockPos bp : pd.placedBlocks) {
            pd.world.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
        }
        exitPortals.remove(origin);

        // Удаляем входной портал
        for (BlockPos bp : entryPortal.placedBlocks) {
            entryPortal.world.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
        }
        hasActivePortal = false;
        bossSpawned     = false;

        LOGGER.info("[RaidManager] cleaned up both portals, raid ended");
    }

    // === Спавн босса (никаких изменений) ===

    public static void spawnBoss(ServerLevel arena) {
        if (bossSpawned || lastTeleportPos == null) return;
        bossSpawned = true;

        List<? extends String> list;
        int hp; double ox, oz;
        switch (currentTier) {
            case 1 -> { list = RaidConfig.COMMON.bossesLevel1.get(); hp = RaidConfig.COMMON.maxHpLevel1.get(); ox = 24; oz = 0; }
            case 2 -> { list = RaidConfig.COMMON.bossesLevel2.get(); hp = RaidConfig.COMMON.maxHpLevel2.get(); ox = 24; oz = 0; }
            case 3 -> { list = RaidConfig.COMMON.bossesLevel3.get(); hp = RaidConfig.COMMON.maxHpLevel3.get(); ox = 0;  oz = -30; }
            default -> { list = RaidConfig.COMMON.bossesLevel1.get(); hp = RaidConfig.COMMON.maxHpLevel1.get(); ox = 0; oz = 0; }
        }

        if (list.isEmpty()) {
            LOGGER.warn("[RaidManager] no bosses configured for tier {}", currentTier);
            return;
        }

        String bossId = list.get(arena.random.nextInt(list.size()));
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES
                .getValue(new ResourceLocation(bossId));
        double sx = lastTeleportPos.x + ox;
        double sy = lastTeleportPos.y + 1;
        double sz = lastTeleportPos.z + oz;

        var ent = type.spawn(arena, new BlockPos((int) sx, (int) sy, (int) sz),
                MobSpawnType.COMMAND);
        if (ent instanceof LivingEntity boss) {
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
            boss.setHealth(hp);
        }

        LOGGER.info("[RaidManager] spawned boss {} at ({},{},{})", bossId, sx, sy, sz);
    }
}
