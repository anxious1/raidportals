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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Set;
import java.util.function.Function;

public class Tier2PortalBlock extends Block {
    private static final ResourceLocation TEMPLATE =
            new ResourceLocation(ModRegistry.MODID, "arena_lvl_1");
    private static final int Y_OFFSET = 6, X_OFFSET = 8;

    public Tier2PortalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity ent) {
        if (worldIn.isClientSide() || !(ent instanceof ServerPlayer player)) return;

        // STEP 1
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 1: {} stepped into Tier1 portal at {}",
                player.getName().getString(), pos);

        // сохраняем портал
        RaidManager.setActivePortalPosAndLevel((ServerLevel) worldIn, pos);
        RaidManager.setActivePortal(true);

        // STEP 2
        ServerLevel raidLevel = player.server.getLevel(ModRegistry.RAID_ARENA);
        if (raidLevel == null) {
            player.sendSystemMessage(Component.literal("§c[Ошибка] raid_arena не найдено!"));
            return;
        }
        var arena = ArenaManager.getOrCreateArena(pos, raidLevel, TEMPLATE);
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 2: arenaData = {}", arena);

        // STEP 3
        double cx = arena.origin.getX() + arena.size.getX()/2.0 + 0.5 + X_OFFSET;
        double cy = arena.origin.getY() + Y_OFFSET;
        double cz = arena.origin.getZ() + arena.size.getZ()/2.0 + 0.5;
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 3: target coords = ({},{},{})", cx, cy, cz);

        // STEP 4: changeDimension с правильным ITeleporter
        player.changeDimension(raidLevel, new ITeleporter() {
            public PortalInfo getPortalInfo(Entity e, ServerLevel dest, float yaw) {
                RaidPortalsMod.LOGGER.info("[Teleport] STEP 4.1: getPortalInfo for dest={} yaw={}",
                        dest.dimension(), yaw);
                // возвращаем наши координаты напрямую
                return new PortalInfo(
                        new Vec3(cx, cy, cz),
                        Vec3.ZERO,
                        e.getYRot(),
                        e.getXRot()
                );
            }

            @Override
            public Entity placeEntity(Entity e, ServerLevel from, ServerLevel to, float yaw,
                                      Function<Boolean, Entity> reposition) {
                RaidPortalsMod.LOGGER.info("[Teleport] STEP 4.2: placeEntity from={} to={}",
                        from.dimension(), to.dimension());

                // false — чтобы не клонировать сущность
                Entity repl = reposition.apply(false);
                if (repl instanceof ServerPlayer sp) {
                    // принудительно ставим позицию
                    sp.setPos(cx, cy, cz);
                    // и отправляем пакет клиенту
                    sp.connection.teleport(cx, cy, cz, sp.getYRot(), sp.getXRot(), Set.of());
                    RaidPortalsMod.LOGGER.info("[Teleport FIX] forced teleportTo({}, {}, {})", cx, cy, cz);

                    // STEP 5: проверяем реальное положение
                    BlockPos actual = sp.blockPosition();
                    RaidPortalsMod.LOGGER.info("[Teleport] STEP 5: after teleport, {} is at {} in {}",
                            sp.getName().getString(), actual, sp.level().dimension());
                    sp.sendSystemMessage(Component.literal(
                            String.format("You are now at %d, %d, %d in %s",
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

        // финальное логирование
        RaidPortalsMod.LOGGER.info("[Teleport] STEP 6: changeDimension completed");
    }
}
