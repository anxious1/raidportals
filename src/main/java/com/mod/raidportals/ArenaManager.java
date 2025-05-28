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
 * – Overworld делится на «ячейки» 50×50 блоков по X и Z;
 * – Каждая ячейка маппится в измерении на координаты (cellX*1000, cellZ*1000);
 * – Шаблон вставляется однократно для каждой ячейки.
 */
public class ArenaManager {
    private static final int OVERSTEP  = 50;    // ширина ячейки в Overworld
    private static final int GRID_STEP = 1000;  // шаг в измерении
    private static final int ARENA_Y   = 100;   // высота вставки арены

    private static final Map<CellKey, ArenaData> ARENAS = new ConcurrentHashMap<>();

    /**
     * Возвращает данные арены (origin + size) для ячейки, куда упал портал.
     * Если ещё не вставлено – делает это.
     */
    public static ArenaData getOrCreateArena(BlockPos portalPos,
                                             ServerLevel raidLevel,
                                             ResourceLocation templateLoc) {
        int cellX = Math.floorDiv(portalPos.getX(), OVERSTEP);
        int cellZ = Math.floorDiv(portalPos.getZ(), OVERSTEP);
        CellKey key = new CellKey(cellX, cellZ);

        return ARENAS.computeIfAbsent(key, k -> {
            StructureTemplate tpl = raidLevel.getStructureManager()
                    .get(templateLoc)
                    .orElse(null);
            if (tpl == null) return null;

            Vec3i size = tpl.getSize();
            // Вычисляем origin в измерении:
            BlockPos origin = new BlockPos(
                    k.x * GRID_STEP - size.getX() / 2,
                    ARENA_Y,
                    k.z * GRID_STEP - size.getZ() / 2
            );

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

    /** Уникальный ключ ячейки по осям в Overworld */
    private static class CellKey {
        final int x, z;
        CellKey(int x, int z) { this.x = x; this.z = z; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof CellKey)) return false;
            CellKey k = (CellKey)o; return k.x==x && k.z==z;
        }
        @Override public int hashCode() { return x*31 + z; }
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
