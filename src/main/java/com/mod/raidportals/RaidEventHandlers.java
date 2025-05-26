package com.mod.raidportals;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;            // вместо старого WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RaidEventHandlers {

    /** Вызывается при загрузке мира — можно инициализировать состояния. */
    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        // TODO: инициализация хранилища активных порталов
    }

    /** Ежети́ковый обработчик — сюда переехало из WorldTickEvent → LevelTickEvent. */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        // TODO: логика случайной генерации портала
    }

    /** Отслеживает смену измерения игроком — пригодится для удаления портала в оверворлде. */
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // TODO: проверка, вышел ли игрок из арены, и закрытие портала
    }

    /** Срабатывает при смерти любого существа — пригодится для появления обратного портала. */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // TODO: если в арене умер наш босс, спавним обратный портал
    }
}
