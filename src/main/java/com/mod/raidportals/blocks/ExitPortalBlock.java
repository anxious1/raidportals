package com.mod.raidportals.blocks;

import com.mod.raidportals.RaidManager;
import com.mod.raidportals.RaidPortalsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

public class ExitPortalBlock extends Block {
    public ExitPortalBlock(BlockBehaviour.Properties props) { super(props); }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity ent) {
        if (worldIn.isClientSide() || !(ent instanceof ServerPlayer player)) return;
        RaidPortalsMod.LOGGER.info("[ExitPortal] {} at {}", player.getName().getString(), pos);

        ServerLevel overworld = RaidManager.getOverworldLevel();
        BlockPos entry = RaidManager.getActivePortalPos();
        if (overworld == null || entry == null) {
            player.sendSystemMessage(Component.literal("§c[Ошибка] Портал не найден!"));
            return;
        }

        // changeDimension обратно в Overworld к entry
        player.changeDimension(overworld, new ITeleporter() {
            public PortalInfo getPortalInfo(Entity e, ServerLevel dest, float yaw) {
                // возвращаем координаты entry
                return new PortalInfo(
                        new Vec3(entry.getX()+0.5, entry.getY()+1, entry.getZ()+0.5),
                        Vec3.ZERO,
                        e.getYRot(), e.getXRot()
                );
            }
            @Override
            public Entity placeEntity(Entity e, ServerLevel from, ServerLevel to, float yaw,
                                      Function<Boolean, Entity> reposition) {
                return reposition.apply(false);
            }
            @Override public boolean isVanilla() { return false; }
        });

        // регистрируем выход
        RaidManager.onPlayerExit(player);

        // удаляем Exit, если все вышли
        if (!RaidManager.getOverworldLevel().dimension().equals(worldIn.dimension())) {
            // если ушёл в Overworld, удаляем Exit-портал
            RaidManager.closeExitPortal((ServerLevel) worldIn);
        }
    }
}
