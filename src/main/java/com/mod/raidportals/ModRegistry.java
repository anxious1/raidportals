package com.mod.raidportals;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "raidportals");
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "raidportals");

    public static final RegistryObject<Block> PLACEHOLDER_BLOCK =
            BLOCKS.register("placeholder_block", () ->
                    new Block(BlockBehaviour.Properties.of()
                            .strength(3f)                     // прочность
                            .mapColor(MapColor.STONE)         // цвет блока на карте
                            .sound(SoundType.STONE)           // звук камня
                    )
            );

    public static final RegistryObject<Item> PLACEHOLDER_ITEM =
            ITEMS.register("placeholder_item", () ->
                    new BlockItem(PLACEHOLDER_BLOCK.get(), new Item.Properties())
            );

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
