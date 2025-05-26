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

public class Tier3PortalBlock extends Block {
    private static final ResourceLocation TEMPLATE =
            new ResourceLocation(ModRegistry.MODID, "arena_lvl_3");
    private static final int Y_OFFSET = 3;

    public Tier3PortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        System.out.println("[RaidPortals] entityInside @ " + pos);
        if (world.isClientSide() || !(entity instanceof ServerPlayer player)) return;

        // Заходим только один раз из Overworld
        if (player.getCommandSenderWorld().dimension() == ModRegistry.RAID_ARENA) {
            System.out.println("[RaidPortals]  -> already in raid_arena, skip");
            return;
        }

        // Берём целевой мир
        ServerLevel raidLevel = player.getServer().getLevel(ModRegistry.RAID_ARENA);
        if (raidLevel == null) {
            System.out.println("[RaidPortals]  -> raid_arena not found!");
            player.sendSystemMessage(Component.literal("§c[Ошибка] raid_arena не найдено!"));
            return;
        }

        // Получаем или генерим арену
        var arena = ArenaManager.getOrCreateArena(pos, raidLevel, TEMPLATE);
        if (arena == null) {
            System.out.println("[RaidPortals]  -> Arena template missing");
            player.sendSystemMessage(Component.literal("[RaidPortals] Шаблон lvl3 не найден"));
            return;
        }

        double cx = arena.origin.getX() + arena.size.getX() / 2.0 + 0.5;
        double cz = arena.origin.getZ() + arena.size.getZ() / 2.0 + 0.5;
        double cy = arena.origin.getY() + Y_OFFSET;
        System.out.println(String.format("[RaidPortals]  -> target center = (%.3f, %.3f, %.3f)", cx, cy, cz));

        // Меняем измерение с правильно реализованным ITeleporter
        player.changeDimension(raidLevel, new ITeleporter() {
            public PortalInfo getPortalInfo(ServerLevel dest, Entity ent, float yaw) {
                System.out.println("[RaidPortals]    -> getPortalInfo()");
                return new PortalInfo(new Vec3(cx, cy, cz),
                        Vec3.ZERO,
                        ent.getYRot(),
                        ent.getXRot());
            }

            @Override
            public Entity placeEntity(Entity ent,
                                      ServerLevel currentWorld,
                                      ServerLevel destWorld,
                                      float yaw,
                                      Function<Boolean, Entity> repositionEntity) {
                System.out.println("[RaidPortals]    -> placeEntity()");
                // создаём новый объект (кросс-дименсион)
                Entity e = repositionEntity.apply(false);
                // телепортируем именно туда
                if (e instanceof ServerPlayer sp) {
                    sp.teleportTo(destWorld, cx, cy, cz, sp.getYRot(), sp.getXRot());
                } else {
                    e.moveTo(cx, cy, cz);
                }
                e.setDeltaMovement(Vec3.ZERO);
                e.fallDistance = 0;
                System.out.println("[RaidPortals]    -> placed at center");
                return e;
            }

            @Override
            public boolean isVanilla() {
                System.out.println("[RaidPortals]    -> isVanilla() = false");
                return false;
            }
        });
        System.out.println("[RaidPortals]  -> changeDimension() called");
    }
}
