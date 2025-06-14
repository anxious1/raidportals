// src/main/java/com/mod/raidportals/blocks/Tier2PortalBlock.java
package com.mod.raidportals.blocks;

import com.mod.raidportals.ArenaManager;
import com.mod.raidportals.RaidManager;
import com.mod.raidportals.RaidPortalsMod;
import com.mod.raidportals.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Set;
import java.util.function.Function;

public class Tier2PortalBlock extends Block {
    private static final ResourceLocation TEMPLATE =
            new ResourceLocation(ModRegistry.MODID, "arena_lvl_1");
    private static final int Y_OFFSET = 6, X_OFFSET = -20, Z_OFFSET = -1;

    public Tier2PortalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    /** 1) Контур (outline), рисующийся при наведении */
    public VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    /** 2) Форма для трассировки луча (ray-trace) — куда “видит” курсор */
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    /** 3) Форма для клика (pick-shape) — куда кликает курсор */
    // В mappings MCP/Forge может называться getPickShape, но в Mojang mappings – getShape
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity ent) {
        // только сервер + только игрок
        if (worldIn.isClientSide() || !(ent instanceof ServerPlayer player)) return;

        var server = player.getServer();
        if (server != null) {
            var advManager = server.getAdvancements();
            var advancement = advManager.getAdvancement(
                    new ResourceLocation(RaidPortalsMod.MODID, "root")
            );
            if (advancement != null) {
                player.getAdvancements().award(advancement, "root");
            }
        }

        // STEP 1 — ваша отлаженная логика
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 1: {} stepped into Tier2 portal at {}",
                player.getName().getString(), pos);

        // --- ДОБАВЛЕНО ДЛЯ БОСС-СПАВНА ---
        RaidManager.setCurrentTier(2);
        // --- конец добавления ---

        // сохраняем портал (как у вас было)
        RaidManager.setActivePortalPosAndLevel((ServerLevel) worldIn, pos);
        RaidManager.setActivePortal(true);

        // STEP 2
        ServerLevel raidLevel = player.server.getLevel(ModRegistry.RAID_ARENA);
        if (raidLevel == null) {
            player.sendSystemMessage(Component.literal("§c[Ошибка] raid_arena не найдено!"));
            return;
        }
        var arena = ArenaManager.getOrCreateArena(raidLevel, TEMPLATE);
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 2: arenaData = {}", arena);

        // STEP 3
        double cx = arena.origin.getX() + arena.size.getX()/2.0 + 0.5 + X_OFFSET;
        double cy = arena.origin.getY() + Y_OFFSET;
        double cz = arena.origin.getZ() + arena.size.getZ()/2.0 + 0.5 + Z_OFFSET;
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 3: target coords = ({},{},{})", cx, cy, cz);

        // --- ДОБАВЛЕНО ДЛЯ БОСС-СПАВНА ---
        RaidManager.setLastTeleportPos(new Vec3(cx, cy, cz));
        // --- конец добавления ---

        // STEP 4 — не трогаем, ваша идеальная телепортация
        player.changeDimension(raidLevel, new ITeleporter() {
            public PortalInfo getPortalInfo(Entity e, ServerLevel dest, float yaw) {
                return new PortalInfo(
                        new Vec3(cx, cy, cz),
                        Vec3.ZERO,
                        e.getYRot(), e.getXRot()
                );
            }
            @Override public Entity placeEntity(Entity e, ServerLevel from, ServerLevel to, float yaw,
                                                Function<Boolean, Entity> reposition) {
                Entity repl = reposition.apply(false);
                if (repl instanceof ServerPlayer sp) {
                    sp.setPos(cx, cy, cz);
                    sp.connection.teleport(cx, cy, cz, sp.getYRot(), sp.getXRot(), Set.of());
                    RaidPortalsMod.LOGGER.info("[Teleport FIX] forced teleportTo({}, {}, {})", cx, cy, cz);
                }
                return repl;
            }
            @Override public boolean isVanilla() { return false; }
        });

        // STEP 5
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 5: changeDimension completed");
    }
}
