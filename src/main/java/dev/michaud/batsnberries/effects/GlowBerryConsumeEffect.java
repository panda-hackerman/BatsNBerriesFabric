package dev.michaud.batsnberries.effects;

import dev.michaud.batsnberries.gamerules.ModGameRules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class GlowBerryConsumeEffect implements ConsumeEffect {

  @Override
  public Type<? extends ConsumeEffect> getType() {
    return ConsumeEffect.Type.APPLY_EFFECTS;
  }

  @Override
  public boolean onConsume(World world, ItemStack stack, LivingEntity user) {

    if (!(world instanceof ServerWorld serverWorld)) {
      return false;
    }

    int duration = serverWorld.getGameRules().getInt(ModGameRules.GLOW_BERRY_EFFECT_DURATION);

    if (duration == 0) {
      return false;
    }

    StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.GLOWING, duration, 0);
    return user.addStatusEffect(effect);
  }
}