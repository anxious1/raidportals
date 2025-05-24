package com.mod.raidportals;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RaidPortalsMod.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RaidPortalsMod.MODID);

    // Существующий плейсхолдер
    public static final RegistryObject<Block> PLACEHOLDER_BLOCK =
            BLOCKS.register("placeholder_block", () ->
                    new Block(BlockBehaviour.Properties.of()
                            .strength(3f)
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE)
                    )
            );
    public static final RegistryObject<Item> PLACEHOLDER_ITEM =
            ITEMS.register("placeholder_item", () ->
                    new BlockItem(PLACEHOLDER_BLOCK.get(), new Item.Properties())
            );

    // ─── Блоки рейд-порталов ─────────────────────────────────────────────────────
    public static final RegistryObject<Block> RAID_PORTAL_LVL1 = BLOCKS.register(
            "raid_portal_lvl1",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
            )
    );
    public static final RegistryObject<Block> RAID_PORTAL_LVL2 = BLOCKS.register(
            "raid_portal_lvl2",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
            )
    );
    public static final RegistryObject<Block> RAID_PORTAL_LVL3 = BLOCKS.register(
            "raid_portal_lvl3",
            () -> new Block(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
            )
    );

    // ─── Предметы для блоков рейд-порталов ───────────────────────────────────────
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

    // Инициализация регистрации на шине событий
    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
