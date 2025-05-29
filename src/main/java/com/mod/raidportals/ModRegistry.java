// src/main/java/com/mod/raidportals/ModRegistry.java
package com.mod.raidportals;

import com.mod.raidportals.blocks.ExitPortalBlock;
import com.mod.raidportals.blocks.Tier1PortalBlock;
import com.mod.raidportals.blocks.Tier2PortalBlock;
import com.mod.raidportals.blocks.Tier3PortalBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModRegistry {
    public static final String MODID = RaidPortalsMod.MODID;
    private static final Logger LOGGER = LogManager.getLogger(MODID); // EDITED

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Tier 1 портал
    public static final RegistryObject<Block> RAID_PORTAL_LVL1 = BLOCKS.register(
            "raid_portal_lvl1",
            () -> new Tier1PortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10))
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL1_ITEM = ITEMS.register(
            "raid_portal_lvl1",
            () -> new BlockItem(RAID_PORTAL_LVL1.get(), new Item.Properties())
    );

    // Tier 2 портал
    public static final RegistryObject<Block> RAID_PORTAL_LVL2 = BLOCKS.register(
            "raid_portal_lvl2",
            () -> new Tier2PortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10))
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL2_ITEM = ITEMS.register(
            "raid_portal_lvl2",
            () -> new BlockItem(RAID_PORTAL_LVL2.get(), new Item.Properties())
    );

    // Tier 3 портал
    public static final RegistryObject<Block> RAID_PORTAL_LVL3 = BLOCKS.register(
            "raid_portal_lvl3",
            () -> new Tier3PortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10))
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL3_ITEM = ITEMS.register(
            "raid_portal_lvl3",
            () -> new BlockItem(RAID_PORTAL_LVL3.get(), new Item.Properties())
    );

    // Exit портал
    public static final RegistryObject<Block> EXIT_PORTAL = BLOCKS.register(
            "exit_portal",
            () -> new ExitPortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10))
    );
    public static final RegistryObject<Item> EXIT_PORTAL_ITEM = ITEMS.register(
            "exit_portal",
            () -> new BlockItem(EXIT_PORTAL.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> RAID_COIN = ITEMS.register(
            "raid_coin",
            () -> new Item(new Item.Properties())
    );

    public static final ResourceKey<DimensionType> RAID_ARENA_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    new ResourceLocation(MODID, "raid_arena"));
    public static final ResourceKey<Level> RAID_ARENA =
            ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(MODID, "raid_arena"));

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        bus.register(ModRegistry.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[RaidPortals] Dimension keys loaded:"); // EDITED
        LOGGER.info("  type: {}", RAID_ARENA_TYPE.location());
        LOGGER.info("  dim:  {}", RAID_ARENA.location());
    }
}
