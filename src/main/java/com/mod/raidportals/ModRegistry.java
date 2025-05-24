package com.mod.raidportals;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModRegistry {
    // МодID
    public static final String MODID = RaidPortalsMod.MODID;

    // ─── Регистрация блоков и предметов ───────────────────────────────────────
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> PLACEHOLDER_BLOCK =
            BLOCKS.register("placeholder_block", () ->
                    new Block(BlockBehaviour.Properties.of()
                            .strength(3f)
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE))
            );
    public static final RegistryObject<Item> PLACEHOLDER_ITEM =
            ITEMS.register("placeholder_item", () ->
                    new BlockItem(PLACEHOLDER_BLOCK.get(), new Item.Properties())
            );

    public static final RegistryObject<Block> RAID_PORTAL_LVL1 = BLOCKS.register(
            "raid_portal_lvl1",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F))
    );
    public static final RegistryObject<Block> RAID_PORTAL_LVL2 = BLOCKS.register(
            "raid_portal_lvl2",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F))
    );
    public static final RegistryObject<Block> RAID_PORTAL_LVL3 = BLOCKS.register(
            "raid_portal_lvl3",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F))
    );

    public static final RegistryObject<Item> RAID_PORTAL_LVL1_ITEM = ITEMS.register(
            "raid_portal_lvl1",
            () -> new BlockItem(RAID_PORTAL_LVL1.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL2_ITEM = ITEMS.register(
            "raid_portal_lvl2",
            () -> new BlockItem(RAID_PORTAL_LVL2.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> RAID_PORTAL_LVL3_ITEM = ITEMS.register(
            "raid_portal_lvl3",
            () -> new BlockItem(RAID_PORTAL_LVL3.get(), new Item.Properties())
    );

    // ─── Константы для измерения рейд-арены ────────────────────────────────────
    /** ResourceKey для типа измерения (dimension_type) */
    public static final ResourceKey<DimensionType> RAID_ARENA_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(MODID, "raid_arena"));

    /** ResourceKey для самого измерения (dimension) */
    public static final ResourceKey<Level> RAID_ARENA =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MODID, "raid_arena"));

    // ─── Инициализация регистрации на шине событий ──────────────────────────
    public static void init(IEventBus bus) {
        // Регистрируем этот класс, чтобы @SubscribeEvent обрабатывался и ключи были объявлены
        bus.register(ModRegistry.class);

        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    /** Подписка на общий этап загрузки, проверяем, что наши ResourceKey создались */
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        System.out.println("[RaidPortals] Loaded dimension keys:");
        System.out.println("  type: " + RAID_ARENA_TYPE.location());
        System.out.println("  dim:  " + RAID_ARENA.location());
    }
}
