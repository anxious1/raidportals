package com.mod.raidportals;

import com.mod.raidportals.advancement.BossKilledTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.level.LevelEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;


@Mod.EventBusSubscriber(modid = RaidPortalsMod.MODID)
public class RaidEventHandlers {
    private static int tickCounter = 0;
    private static final Logger LOGGER = RaidPortalsMod.LOGGER;

    @SubscribeEvent
    public static void onWorldLoad(Load event) {
        if (event.getLevel().isClientSide()) return;
        ServerLevel lvl = (ServerLevel) event.getLevel();
        if (lvl.dimension() == Level.OVERWORLD) {
            tickCounter = 0;
            LOGGER.info("[RaidEvent] World loaded, tickCounter reset");
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != Phase.END) return;
        if (!(event.level instanceof ServerLevel server)) return;
        if (server.dimension() != Level.OVERWORLD) return;

        tickCounter++;
        if (tickCounter < 1200) return;
        tickCounter = 0;

        for (ServerPlayer player : server.players()) {
            BlockPos center = player.blockPosition();
            int tier = server.getRandom().nextInt(3) + 1;
            boolean success = RaidManager.spawnRaidPortal(server, tier, center);
            if (success) {
                LOGGER.info("[RaidEvent] Portal Tier{} spawned at {}", tier, center);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getFrom() == Level.OVERWORLD && event.getTo() == ModRegistry.RAID_ARENA) {
            ServerLevel arena = player.server.getLevel(ModRegistry.RAID_ARENA);
            if (arena == null) return;

            RaidManager.addParticipant(player);
            RaidManager.spawnBoss(arena);
            LOGGER.info("[RaidEvent] spawnBoss() called for Tier{} in {}", RaidManager.getCurrentTier(), arena.dimension());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity e = event.getEntity();
        Level rawLevel = e.level();
        if (!(rawLevel instanceof ServerLevel arena)) return;
        if (arena.dimension() != ModRegistry.RAID_ARENA) return;

        String id = ForgeRegistries.ENTITY_TYPES.getKey(e.getType()).toString();
        boolean isBoss =
                RaidConfig.COMMON.bossesLevel1.get().contains(id) ||
                        RaidConfig.COMMON.bossesLevel2.get().contains(id) ||
                        RaidConfig.COMMON.bossesLevel3.get().contains(id);
        if (!isBoss) return;

        // Определяем tier босса
        int tier = RaidConfig.COMMON.bossesLevel1.get().contains(id) ? 1
                : RaidConfig.COMMON.bossesLevel2.get().contains(id) ? 2
                : 3;

        DamageSource src = event.getSource();
        if (src.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("[RaidEvent] onLivingDeath: boss={} tier={} killed by={}", id, tier, player.getName().getString());
            // Вызов вашего триггера
            BossKilledTrigger.INSTANCE.trigger(player, tier);
            LOGGER.info("[RaidEvent] Advancement trigger fired for tier={}", tier);
        }

        // Спавним монеты
        RandomSource rnd = arena.getRandom();
        int coins = 5 + rnd.nextInt(6);
        ItemEntity coinEntity = new ItemEntity(arena, e.getX(), e.getY()+1, e.getZ(),
                new ItemStack(ModRegistry.RAID_COIN.get(), coins));
        arena.addFreshEntity(coinEntity);

        // Спавним выходной портал
        BlockPos deathPos = e.blockPosition();
        RaidManager.spawnExitPortal(arena, deathPos);
        LOGGER.info("[RaidEvent] Exit portal spawned at {}", deathPos.above());
    }

    @SubscribeEvent
    public static void onBossDrops(LivingDropsEvent event) {
        LivingEntity e = event.getEntity();
        Level rawLevel = e.level();
        if (!(rawLevel instanceof ServerLevel arena)) return;
        if (arena.dimension() != ModRegistry.RAID_ARENA) return;

        String id = ForgeRegistries.ENTITY_TYPES.getKey(e.getType()).toString();
        boolean isBoss =
                RaidConfig.COMMON.bossesLevel1.get().contains(id) ||
                        RaidConfig.COMMON.bossesLevel2.get().contains(id) ||
                        RaidConfig.COMMON.bossesLevel3.get().contains(id);
        if (!isBoss) return;

        double mult = switch (RaidManager.getCurrentTier()) {
            case 1 -> 1.5;
            case 2 -> 2.0;
            case 3 -> 3.0;
            default -> 1.0;
        };

        for (ItemEntity drop : event.getDrops()) {
            int oldCount = drop.getItem().getCount();
            drop.getItem().setCount((int)Math.ceil(oldCount * mult));
        }
    }
}
