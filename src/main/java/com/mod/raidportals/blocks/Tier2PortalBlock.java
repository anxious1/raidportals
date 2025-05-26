package com.mod.raidportals.blocks;

import com.mod.raidportals.ArenaManager;
import com.mod.raidportals.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class Tier2PortalBlock extends Block {
    private static final ResourceLocation TEMPLATE =
            new ResourceLocation(ModRegistry.MODID, "arena_lvl_1");
    private static final int Y_OFFSET = 6;

    public Tier2PortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        System.out.println("[RaidPortals][Tier2] entityInside @ " + pos);
        if (world.isClientSide() || !(entity instanceof ServerPlayer player)) return;

        if (player.getCommandSenderWorld().dimension() == ModRegistry.RAID_ARENA) {
            System.out.println("[RaidPortals][Tier2]  -> already in raid_arena, skip");
            return;
        }

        ServerLevel raidLevel = player.getServer().getLevel(ModRegistry.RAID_ARENA);
        if (raidLevel == null) {
            player.sendSystemMessage(Component.literal("§c[Ошибка] raid_arena не найдено!"));
            return;
        }

        ArenaManager.ArenaData arena = ArenaManager.getOrCreateArena(pos, raidLevel, TEMPLATE);
        if (arena == null) {
            player.sendSystemMessage(Component.literal("[RaidPortals] Шаблон lvl2 не найден"));
            return;
        }

        double cx = arena.origin.getX() + arena.size.getX() / 2.0 + 0.5;
        double cz = arena.origin.getZ() + arena.size.getZ() / 2.0 + 0.5;
        double cy = arena.origin.getY() + Y_OFFSET;
        System.out.printf("[RaidPortals][Tier2] target center = (%.3f, %.3f, %.3f)%n", cx, cy, cz);

        player.changeDimension(raidLevel, new ITeleporter() {
            public PortalInfo getPortalInfo(ServerLevel dest, Entity ent, float yaw) {
                System.out.println("[RaidPortals][Tier2]  -> getPortalInfo");
                return new PortalInfo(
                        new Vec3(cx, cy, cz),
                        Vec3.ZERO,
                        ent.getYRot(),
                        ent.getXRot()
                );
            }

            @Override
            public Entity placeEntity(Entity ent,
                                      ServerLevel currentWorld,
                                      ServerLevel destWorld,
                                      float yaw,
                                      Function<Boolean, Entity> repositionEntity) {
                System.out.println("[RaidPortals][Tier2]  -> placeEntity");
                Entity e = repositionEntity.apply(false);
                if (e instanceof ServerPlayer sp) {
                    sp.teleportTo(destWorld, cx, cy, cz, sp.getYRot(), sp.getXRot());
                } else {
                    e.moveTo(cx, cy, cz);
                }
                e.setDeltaMovement(Vec3.ZERO);
                e.fallDistance = 0;
                return e;
            }

            @Override
            public boolean isVanilla() {
                return false;
            }
        });
        System.out.println("[RaidPortals][Tier2]  -> changeDimension called");
    }
}