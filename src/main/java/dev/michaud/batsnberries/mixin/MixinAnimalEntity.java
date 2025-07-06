package dev.michaud.batsnberries.mixin;

import dev.michaud.batsnberries.events.ModEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class MixinAnimalEntity extends PassiveEntity {

  protected MixinAnimalEntity(EntityType<? extends PassiveEntity> entityType,
      World world) {
    super(entityType, world);
  }

  @Inject(method = "eat", at = @At("HEAD"))
  private void afterEat(final PlayerEntity player, final Hand hand, final ItemStack stack,
      final CallbackInfo ci) {
    // Call callbacks :)
    ModEvents.ANIMAL_FED.invoker().interact(player, hand, stack, (AnimalEntity) (Object) this);
  }
}