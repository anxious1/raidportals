package com.mod.raidportals;

import com.mod.raidportals.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RaidPortalsMod.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RaidPortalsMod.MODID);

    public static final RegistryObject<Item> TIER1_RAID_COIN = ITEMS.register(
            "tier1_raid_coin",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> TIER2_RAID_COIN = ITEMS.register(
            "tier2_raid_coin",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> TIER3_RAID_COIN = ITEMS.register(
            "tier3_raid_coin",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<CreativeModeTab> RAID_PORTALS_TAB =
            TABS.register("raid_portals", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + MODID + ".raid_portals"))  // Используем Component, не ResourceLocation :contentReference[oaicite:0]{index=0}
                            .icon(() -> new ItemStack(TIER1_RAID_COIN.get()))
                            .displayItems((params, output) -> {
                                output.accept(TIER1_RAID_COIN.get());
                                output.accept(TIER2_RAID_COIN.get());
                                output.accept(TIER3_RAID_COIN.get());
                            })
                            .build()
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
        TABS.register(bus);
        TILE_ENTITIES.register(bus);
        bus.register(ModRegistry.class);
    }
    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_00 = BLOCKS.register(
            "tier1_piece_00",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_00_ITEM = ITEMS.register(
            "tier1_piece_00",
            () -> new BlockItem(TIER1_PIECE_00.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_01 = BLOCKS.register(
            "tier1_piece_01",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_01_ITEM = ITEMS.register(
            "tier1_piece_01",
            () -> new BlockItem(TIER1_PIECE_01.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_02 = BLOCKS.register(
            "tier1_piece_02",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_02_ITEM = ITEMS.register(
            "tier1_piece_02",
            () -> new BlockItem(TIER1_PIECE_02.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_10 = BLOCKS.register(
            "tier1_piece_10",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_10_ITEM = ITEMS.register(
            "tier1_piece_10",
            () -> new BlockItem(TIER1_PIECE_10.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_11 = BLOCKS.register(
            "tier1_piece_11",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_11_ITEM = ITEMS.register(
            "tier1_piece_11",
            () -> new BlockItem(TIER1_PIECE_11.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_12 = BLOCKS.register(
            "tier1_piece_12",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_12_ITEM = ITEMS.register(
            "tier1_piece_12",
            () -> new BlockItem(TIER1_PIECE_12.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_20 = BLOCKS.register(
            "tier1_piece_20",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_20_ITEM = ITEMS.register(
            "tier1_piece_20",
            () -> new BlockItem(TIER1_PIECE_20.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_21 = BLOCKS.register(
            "tier1_piece_21",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_21_ITEM = ITEMS.register(
            "tier1_piece_21",
            () -> new BlockItem(TIER1_PIECE_21.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER1_PIECE_22 = BLOCKS.register(
            "tier1_piece_22",
            () -> new Tier1PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER1_PIECE_22_ITEM = ITEMS.register(
            "tier1_piece_22",
            () -> new BlockItem(TIER1_PIECE_22.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_00 = BLOCKS.register(
            "tier2_piece_00",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_00_ITEM = ITEMS.register(
            "tier2_piece_00",
            () -> new BlockItem(TIER2_PIECE_00.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_01 = BLOCKS.register(
            "tier2_piece_01",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_01_ITEM = ITEMS.register(
            "tier2_piece_01",
            () -> new BlockItem(TIER2_PIECE_01.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_02 = BLOCKS.register(
            "tier2_piece_02",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_02_ITEM = ITEMS.register(
            "tier2_piece_02",
            () -> new BlockItem(TIER2_PIECE_02.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_10 = BLOCKS.register(
            "tier2_piece_10",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_10_ITEM = ITEMS.register(
            "tier2_piece_10",
            () -> new BlockItem(TIER2_PIECE_10.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_11 = BLOCKS.register(
            "tier2_piece_11",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_11_ITEM = ITEMS.register(
            "tier2_piece_11",
            () -> new BlockItem(TIER2_PIECE_11.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_12 = BLOCKS.register(
            "tier2_piece_12",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_12_ITEM = ITEMS.register(
            "tier2_piece_12",
            () -> new BlockItem(TIER2_PIECE_12.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_20 = BLOCKS.register(
            "tier2_piece_20",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_20_ITEM = ITEMS.register(
            "tier2_piece_20",
            () -> new BlockItem(TIER2_PIECE_20.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_21 = BLOCKS.register(
            "tier2_piece_21",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_21_ITEM = ITEMS.register(
            "tier2_piece_21",
            () -> new BlockItem(TIER2_PIECE_21.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER2_PIECE_22 = BLOCKS.register(
            "tier2_piece_22",
            () -> new Tier2PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER2_PIECE_22_ITEM = ITEMS.register(
            "tier2_piece_22",
            () -> new BlockItem(TIER2_PIECE_22.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_00 = BLOCKS.register(
            "tier3_piece_00",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_00_ITEM = ITEMS.register(
            "tier3_piece_00",
            () -> new BlockItem(TIER3_PIECE_00.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_01 = BLOCKS.register(
            "tier3_piece_01",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_01_ITEM = ITEMS.register(
            "tier3_piece_01",
            () -> new BlockItem(TIER3_PIECE_01.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_02 = BLOCKS.register(
            "tier3_piece_02",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_02_ITEM = ITEMS.register(
            "tier3_piece_02",
            () -> new BlockItem(TIER3_PIECE_02.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_10 = BLOCKS.register(
            "tier3_piece_10",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_10_ITEM = ITEMS.register(
            "tier3_piece_10",
            () -> new BlockItem(TIER3_PIECE_10.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_11 = BLOCKS.register(
            "tier3_piece_11",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_11_ITEM = ITEMS.register(
            "tier3_piece_11",
            () -> new BlockItem(TIER3_PIECE_11.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_12 = BLOCKS.register(
            "tier3_piece_12",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_12_ITEM = ITEMS.register(
            "tier3_piece_12",
            () -> new BlockItem(TIER3_PIECE_12.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_20 = BLOCKS.register(
            "tier3_piece_20",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_20_ITEM = ITEMS.register(
            "tier3_piece_20",
            () -> new BlockItem(TIER3_PIECE_20.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_21 = BLOCKS.register(
            "tier3_piece_21",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_21_ITEM = ITEMS.register(
            "tier3_piece_21",
            () -> new BlockItem(TIER3_PIECE_21.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER3_PIECE_22 = BLOCKS.register(
            "tier3_piece_22",
            () -> new Tier3PortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER3_PIECE_22_ITEM = ITEMS.register(
            "tier3_piece_22",
            () -> new BlockItem(TIER3_PIECE_22.get(), new Item.Properties())
    );


    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_00 = BLOCKS.register(
            "tier0_piece_00",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_00_ITEM = ITEMS.register(
            "tier0_piece_00",
            () -> new BlockItem(TIER0_PIECE_00.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_01 = BLOCKS.register(
            "tier0_piece_01",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_01_ITEM = ITEMS.register(
            "tier0_piece_01",
            () -> new BlockItem(TIER0_PIECE_01.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_02 = BLOCKS.register(
            "tier0_piece_02",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_02_ITEM = ITEMS.register(
            "tier0_piece_02",
            () -> new BlockItem(TIER0_PIECE_02.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_10 = BLOCKS.register(
            "tier0_piece_10",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_10_ITEM = ITEMS.register(
            "tier0_piece_10",
            () -> new BlockItem(TIER0_PIECE_10.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_11 = BLOCKS.register(
            "tier0_piece_11",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_11_ITEM = ITEMS.register(
            "tier0_piece_11",
            () -> new BlockItem(TIER0_PIECE_11.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_12 = BLOCKS.register(
            "tier0_piece_12",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_12_ITEM = ITEMS.register(
            "tier0_piece_12",
            () -> new BlockItem(TIER0_PIECE_12.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_20 = BLOCKS.register(
            "tier0_piece_20",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_20_ITEM = ITEMS.register(
            "tier0_piece_20",
            () -> new BlockItem(TIER0_PIECE_20.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_21 = BLOCKS.register(
            "tier0_piece_21",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_21_ITEM = ITEMS.register(
            "tier0_piece_21",
            () -> new BlockItem(TIER0_PIECE_21.get(), new Item.Properties())
    );

    // Регистрируем блок
    public static final RegistryObject<Block> TIER0_PIECE_22 = BLOCKS.register(
            "tier0_piece_22",
            () -> new ExitPortalBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 10)
            )
    );

    // ... и регистрируйте к нему BlockItem по тому же имени:
    public static final RegistryObject<Item> TIER0_PIECE_22_ITEM = ITEMS.register(
            "tier0_piece_22",
            () -> new BlockItem(TIER0_PIECE_22.get(), new Item.Properties())
    );


    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[RaidPortals] Dimension keys loaded:"); // EDITED
        LOGGER.info("  type: {}", RAID_ARENA_TYPE.location());
        LOGGER.info("  dim:  {}", RAID_ARENA.location());
    }
}
