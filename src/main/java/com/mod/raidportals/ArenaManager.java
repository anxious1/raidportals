// src/main/java/com/mod/raidportals/ArenaManager.java
package com.mod.raidportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер вставки арены для порталов:
 * – Новая арена генерируется по радиальному алгоритму (кольца шагом GRID_STEP вокруг (0,0));
 * – Каждая конкретная входная структура (entryPortal.origin) порождает ровно одну арену;
 * – Повторные вызовы для того же entryPortal.origin будут возвращать уже вставленную ArenaData.
 */
public class ArenaManager {
    private static final int GRID_STEP = 1000;  // радиус шага колец
    private static final int ARENA_Y   = 100;   // высота вставки арены

    // Карта: для каждой origin входного портала — своя арена
    private static final Map<BlockPos, ArenaData> ARENAS = new ConcurrentHashMap<>();

    // Индекс для очередной арены в кольцевом порядке
    private static int nextArenaIndex = 0;

    /**
     * Возвращает данные арены для текущего entry-portal.
     * Если для этого entryPortal.origin арена ещё не создана — вставляет структуру.
     */
    public static ArenaData getOrCreateArena(ServerLevel raidLevel, ResourceLocation templateLoc) {
        // Ключ — именно origin входного портала
        BlockPos entryOrigin = RaidManager.getEntryPortalPos();
        if (entryOrigin == null) {
            return null; // портал ещё не заспавнен
        }

        return ARENAS.computeIfAbsent(entryOrigin, originKey -> {
            // Загружаем шаблон
            StructureTemplate tpl = raidLevel.getStructureManager()
                    .get(templateLoc)
                    .orElse(null);
            if (tpl == null) return null;
            Vec3i size = tpl.getSize();

            // Рассчитываем радиальную позицию для этой арены
            int n = nextArenaIndex++;
            int layer = (int) Math.ceil((Math.sqrt(n + 1) - 1) / 2);
            int legLen = layer * 2;
            int maxPrev = layer > 1 ? (2 * (layer - 1) - 1) * (2 * (layer - 1) - 1) : 0;
            int indexInLayer = n - maxPrev;
            int side = indexInLayer / (legLen+1);       // 0..3
            int offset = indexInLayer % (legLen+1);     // 0..legLen-1

            int dx, dz;
            switch (side) {
                case 0 -> { dx = layer;        dz = -layer + offset; }
                case 1 -> { dx = layer - offset; dz = layer;      }
                case 2 -> { dx = -layer;       dz = layer - offset; }
                default -> { dx = -layer + offset; dz = -layer;    }
            }

            int x = dx * GRID_STEP;
            int z = dz * GRID_STEP;

            // Центрируем шаблон по его размеру
            BlockPos origin = new BlockPos(
                    x - size.getX() / 2,
                    ARENA_Y,
                    z - size.getZ() / 2
            );

            // Вставляем структуру в мир
            tpl.placeInWorld(
                    raidLevel,
                    origin,
                    origin,
                    new StructurePlaceSettings(),
                    raidLevel.random,
                    2
            );

            return new ArenaData(origin, size);
        });
    }

    /** Данные вставленной арены: origin и размер шаблона */
    public static class ArenaData {
        public final BlockPos origin;
        public final Vec3i    size;
        public ArenaData(BlockPos origin, Vec3i size) {
            this.origin = origin;
            this.size   = size;
        }
    }
}
