package dev.michaud.batsnberries.events.handlers;

import dev.michaud.batsnberries.effects.GlowBerryConsumeEffect;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Items;

/** Makes glow berries always edible and give the glowing effect */
public class GlowBerryComponentModifyHandler {

  public static void initializeCallbacks() {

    DefaultItemComponentEvents.MODIFY.register(context -> {
      context.modify(Items.GLOW_BERRIES, builder -> {

        final FoodComponent defaultFoodComponent = builder
            .getOrDefault(DataComponentTypes.FOOD, FoodComponents.GLOW_BERRIES);

        /* Add the consumable component and modify the food component*/
        builder.add(DataComponentTypes.CONSUMABLE,
            ConsumableComponents.food()
                .consumeEffect(new GlowBerryConsumeEffect())
                .build())
            .add(DataComponentTypes.FOOD, new FoodComponent(
                defaultFoodComponent.nutrition(),
                defaultFoodComponent.saturation(),
                true));
      });
    });
  }

}