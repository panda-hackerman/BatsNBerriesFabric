package dev.michaud.batsnberries.items;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModItems {

  public static final Item BAT_BUCKET = Registry.register(Registries.ITEM, BatBucket.KEY,
      new BatBucket(new Item.Settings()
          .maxCount(1)
          .component(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT)
          .registryKey(BatBucket.KEY)));

  public static void registerModItems() {

    // Make dispenser behave properly
    DispenserBlock.registerBehavior(ModItems.BAT_BUCKET, new ItemDispenserBehavior() {
      private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

      @Override
      public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        FluidModificationItem item = (FluidModificationItem) stack.getItem();
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        World world = pointer.world();
        if (item.placeFluid(null, world, blockPos, null)) {
          item.onEmptied(null, world, stack, blockPos);
          return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.BUCKET));
        } else {
          return this.fallbackBehavior.dispense(pointer, stack);
        }
      }
    });
  }

}