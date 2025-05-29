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
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.phys.Vec3;

public class ExitPortalBlock extends Block {
    public ExitPortalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity ent) {
        // только сервер + только игрок + ещё не выходил
        if (worldIn.isClientSide() || !(ent instanceof ServerPlayer player)
                || RaidManager.hasExitedThrough(pos, player.getUUID())) {
            return;
        }

        RaidPortalsMod.LOGGER.info("[ExitTeleport] STEP 1: {} entered ExitPortal at {}",
                player.getName().getString(), pos);

        // Получаем данные для возврата
        RaidManager.onPlayerExit(pos, player.getUUID());
        ServerLevel returnWorld = RaidManager.getOverworldLevel();
        BlockPos entryPos    = RaidManager.getEntryPortalPos();
        if (returnWorld == null || entryPos == null) {
            player.sendSystemMessage(Component.literal("§c[Ошибка] Портал не найден для возврата!"));
            return;
        }
        RaidPortalsMod.LOGGER.info("[ExitTeleport] STEP 2: returnWorld={} entryPos={}", returnWorld.dimension().location(), entryPos);

        // Вычисляем координаты возврата
        double tx = entryPos.getX() + 0.5;
        double ty = entryPos.getY() + 1.0;
        double tz = entryPos.getZ() + 0.5;
        RaidPortalsMod.LOGGER.info("[ExitTeleport] STEP 3: target coords = ({},{},{})", tx, ty, tz);

        // Телепортируем через changeDimension + ITeleporter
        player.changeDimension(returnWorld, new ITeleporter() {
            public PortalInfo getPortalInfo(Entity e, ServerLevel dest, float yaw) {
                // без движения, с сохраненным разворотом
                return new PortalInfo(new Vec3(tx, ty, tz), Vec3.ZERO, e.getYRot(), e.getXRot());
            }

            @Override
            public Entity placeEntity(Entity e, ServerLevel from, ServerLevel to, float yaw,
                                      Function<Boolean, Entity> reposition) {
                Entity repl = reposition.apply(false);
                if (repl instanceof ServerPlayer sp) {
                    sp.connection.teleport(tx, ty, tz, sp.getYRot(), sp.getXRot(), Set.of());
                    RaidPortalsMod.LOGGER.info("[ExitTeleport] STEP 4: forced teleportTo({}, {}, {})", tx, ty, tz);

                    // Проверка позиции
                    BlockPos actual = sp.blockPosition();
                    RaidPortalsMod.LOGGER.info("[ExitTeleport] STEP 5: after teleport, {} is at {} in {}",
                            sp.getName().getString(), actual, sp.level().dimension());

                    sp.sendSystemMessage(Component.literal(
                            String.format("You have returned to %d, %d, %d in %s",
                                    actual.getX(), actual.getY(), actual.getZ(),
                                    sp.level().dimension().location()
                            )
                    ));
                }
                return repl;
            }

            @Override
            public boolean isVanilla() {
                return false;
            }
        });

        RaidPortalsMod.LOGGER.info("[ExitTeleport] STEP 6: changeDimension completed");
    }
}
