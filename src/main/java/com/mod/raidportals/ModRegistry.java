package com.mod.raidportals;

import com.mod.raidportals.blocks.Tier1PortalBlock;
import com.mod.raidportals.blocks.Tier2PortalBlock;
import com.mod.raidportals.blocks.Tier3PortalBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Регистрация блоков, предметов и ключей измерения.
 */
public class ModRegistry {
    public static final String MODID = RaidPortalsMod.MODID;

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Портал первого тира
    public static final RegistryObject<Block> RAID_PORTAL_LVL1 = BLOCKS.register(
            "raid_portal_lvl1",
            () -> new Tier1PortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10)
            )
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL1_ITEM = ITEMS.register(
            "raid_portal_lvl1",
            () -> new BlockItem(RAID_PORTAL_LVL1.get(), new Item.Properties())
    );

    // Портал второго тира
    public static final RegistryObject<Block> RAID_PORTAL_LVL2 = BLOCKS.register(
            "raid_portal_lvl2",
            () -> new Tier2PortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10)
            )
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL2_ITEM = ITEMS.register(
            "raid_portal_lvl2",
            () -> new BlockItem(RAID_PORTAL_LVL2.get(), new Item.Properties())
    );

    // Портал третьего тира
    public static final RegistryObject<Block> RAID_PORTAL_LVL3 = BLOCKS.register(
            "raid_portal_lvl3",
            () -> new Tier3PortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 10)
            )
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL3_ITEM = ITEMS.register(
            "raid_portal_lvl3",
            () -> new BlockItem(RAID_PORTAL_LVL3.get(), new Item.Properties())
    );


    // Ключи для измерения raid_arena
    public static final ResourceKey<DimensionType> RAID_ARENA_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(MODID, "raid_arena"));
    public static final ResourceKey<Level> RAID_ARENA =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MODID, "raid_arena"));

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        bus.register(ModRegistry.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        System.out.println("[RaidPortals] Dimension keys loaded:");
        System.out.println("  type: " + RAID_ARENA_TYPE.location());
        System.out.println("  dim:  " + RAID_ARENA.location());
    }
}
