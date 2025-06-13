// src/main/java/com/mod/raidportals/advancement/BossKilledTrigger.java
package com.mod.raidportals.advancement;

import com.google.gson.JsonObject;
import com.mod.raidportals.RaidPortalsMod;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Custom trigger for killing a raid boss of a given tier.
 */
public class BossKilledTrigger extends SimpleCriterionTrigger<BossKilledTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(RaidPortalsMod.MODID, "boss_killed");
    public static final BossKilledTrigger INSTANCE = new BossKilledTrigger();

    // Private no-arg constructor; SimpleCriterionTrigger has a no-arg super()
    private BossKilledTrigger() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json,
                                      ContextAwarePredicate playerPredicate,
                                      DeserializationContext context) {
        int tier = json.has("tier") ? json.get("tier").getAsInt() : 1;
        return new Instance(playerPredicate, tier);
    }

    /**
     * Call this when a player kills a boss of the given tier.
     * Grants the criterion to any advancement definitions listening for that tier.
     */
    public void trigger(ServerPlayer player, int tier) {
        this.trigger(player, inst -> inst.matches(tier));
    }

    /** Criterion instance storing the required tier. */
    public static class Instance extends AbstractCriterionTriggerInstance {
        private final int tier;

        public Instance(ContextAwarePredicate playerPredicate, int tier) {
            super(ID, playerPredicate);
            this.tier = tier;
        }

        /** Returns true if this instance’s tier matches the killed boss’s tier. */
        public boolean matches(int otherTier) {
            return this.tier == otherTier;
        }

        /** Include the "tier" field when serializing back to JSON. */
        @Override
        public JsonObject serializeToJson(SerializationContext ctx) {
            JsonObject obj = super.serializeToJson(ctx);
            obj.addProperty("tier", tier);
            return obj;
        }
    }
}
