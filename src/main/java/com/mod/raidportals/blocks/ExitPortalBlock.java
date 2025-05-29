// src/main/java/com/mod/raidportals/blocks/ExitPortalBlock.java
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

public class ExitPortalBlock extends Block {
    public ExitPortalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity ent) {
        // сервер + игрок + ещё не выходил через этот портал
        if (worldIn.isClientSide()
                || !(ent instanceof ServerPlayer player)
                || RaidManager.hasExitedThrough(pos, player.getUUID())) {
            return;
        }

        RaidPortalsMod.LOGGER.info("[ExitPortal] {} entered portal at {}", player.getName().getString(), pos);

        ServerLevel returnWorld = RaidManager.getOverworldLevel();
        BlockPos returnPos      = RaidManager.getEntryPortalPos();
        if (returnWorld == null || returnPos == null) {
            player.sendSystemMessage(Component.literal("§c[Ошибка] Портал не найден!"));
            return;
        }

        // Регистрируем выход сразу, чтобы предотвратить дублирование
        RaidManager.onPlayerExit(pos, player.getUUID());

        // Телепортируем один раз здесь
        player.teleportTo(
                returnWorld,
                returnPos.getX() + 0.5,
                returnPos.getY() + 1.0,
                returnPos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
        RaidPortalsMod.LOGGER.info("[ExitPortal] teleported {} back to {}", player.getName().getString(), returnPos);
    }
}
