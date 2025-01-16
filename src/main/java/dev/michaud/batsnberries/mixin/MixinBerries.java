package dev.michaud.batsnberries.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.michaud.batsnberries.effects.GlowBerryConsumeEffect;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Items.class)
public abstract class MixinBerries {

  @ModifyExpressionValue(
      method = "<clinit>",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/item/Item$Settings;food(Lnet/minecraft/component/type/FoodComponent;)Lnet/minecraft/item/Item$Settings;")
  )
  private static Item.Settings makeGlowBerriesGlow(Item.Settings original) {

    FoodComponent defaultFoodComponent = FoodComponents.GLOW_BERRIES;
    FoodComponent foodComponent = new FoodComponent.Builder()
        .nutrition(defaultFoodComponent.nutrition())
        .saturationModifier(defaultFoodComponent.saturation())
        .alwaysEdible()
        .build();

    ConsumableComponent consumableComponent = ConsumableComponents.food()
        .consumeEffect(new GlowBerryConsumeEffect())
        .build();

    return original.food(foodComponent, consumableComponent);
  }

}