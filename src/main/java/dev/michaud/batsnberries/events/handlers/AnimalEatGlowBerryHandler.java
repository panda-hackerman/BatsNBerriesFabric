package dev.michaud.batsnberries.events.handlers;

import dev.michaud.batsnberries.events.ModEvents;
import dev.michaud.batsnberries.gamerules.ModGameRules;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

/**
 * Makes animals being fed a glow berry (e.g. from breeding) give the glowing effect. Only applies
 * to foxes in vanilla.
 */
public class AnimalEatGlowBerryHandler {

  public static void initializeCallbacks() {

    ModEvents.ANIMAL_FED.register((player, hand, stack, animal) -> {
      if (!stack.isOf(Items.GLOW_BERRIES)) {
        return;
      }

      if (!(animal.getWorld() instanceof ServerWorld world)) {
        return;
      }

      final int duration = world.getGameRules().getInt(ModGameRules.GLOW_BERRY_EFFECT_DURATION);
      if (duration == 0) {
        return;
      }

      animal.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, duration, 0), player);
    });

  }

}