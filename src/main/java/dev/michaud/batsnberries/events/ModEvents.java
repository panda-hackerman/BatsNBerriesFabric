package dev.michaud.batsnberries.events;

import dev.michaud.batsnberries.mixin.MixinAnimalEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ModEvents {

  /**
   * Callback for when an animal eats something. Not cancellable because there's too many cases,
   * and I'm lazy.
   * @implNote Called at the head of {@link AnimalEntity#eat(PlayerEntity, Hand, ItemStack)}. If the
   * method later returns early (e.g. from another mod) the callbacks will still be called.
   * @see MixinAnimalEntity
   */
  public static final Event<AnimalFedCallback> ANIMAL_FED = EventFactory.createArrayBacked(
      AnimalFedCallback.class,
      (listeners) -> (player, hand, stack, animal) -> {
        for (AnimalFedCallback listener : listeners) {
          listener.interact(player, hand, stack, animal);
        }
      });

  @FunctionalInterface
  public interface AnimalFedCallback {
    /** @see ModEvents#ANIMAL_FED */
    void interact(PlayerEntity player, Hand hand, ItemStack stack, AnimalEntity animal);
  }

}