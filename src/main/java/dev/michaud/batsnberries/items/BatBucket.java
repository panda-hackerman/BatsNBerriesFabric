package dev.michaud.batsnberries.items;

import dev.michaud.batsnberries.BatsNBerriesMod;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class BatBucket extends EntityBucketItem implements PolymerItem {

  public static final RegistryKey<Item> KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(
      BatsNBerriesMod.MOD_ID, "bat_bucket"));

  public BatBucket(Item.Settings settings) {
    super(EntityType.BAT, Fluids.EMPTY, SoundEvents.ITEM_BUCKET_EMPTY_FISH, settings);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {

    if (context.getWorld().isClient) {
      return ActionResult.PASS;
    }

    if (context.getPlayer() != null) {
      return use(context.getWorld(), context.getPlayer(), context.getHand());
    }

    return super.useOnBlock(context);
  }

  @Override
  public ActionResult use(World world, PlayerEntity player, Hand hand) {

    ItemStack itemStack = player.getStackInHand(hand);
    BlockHitResult blockHitResult = raycast(world, player, RaycastContext.FluidHandling.NONE);

    if (blockHitResult.getType() == HitResult.Type.MISS) {
      return ActionResult.PASS;
    } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
      return ActionResult.PASS;
    }

    BlockPos blockPos = blockHitResult.getBlockPos();
    Direction direction = blockHitResult.getSide();
    BlockPos blockPos2 = blockPos.offset(direction);

    if (!world.canPlayerModifyAt(player, blockPos) || !player.canPlaceOn(blockPos2, direction,
        itemStack)) {
      return ActionResult.FAIL;
    }

    if (placeFluid(player, world, blockPos2, blockHitResult)) {

      onEmptied(player, world, itemStack, blockPos2);

      if (player instanceof ServerPlayerEntity) {
        Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) player, blockPos2, itemStack);
      }

      player.incrementStat(Stats.USED.getOrCreateStat(this));

      ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player,
          getEmptiedStack(itemStack, player));
      player.setStackInHand(hand, itemStack2);

      return ActionResult.SUCCESS.withNewHandStack(itemStack2);
    }

    return ActionResult.FAIL;
  }

  @Override
  public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos,
      @Nullable BlockHitResult hitResult) {

    BlockState blockState = world.getBlockState(pos);
    boolean canPlaceFluid = blockState.canBucketPlace(Fluids.EMPTY);

    if (!blockState.isAir() && !canPlaceFluid) {
      return hitResult != null && this.placeFluid(player, world,
          hitResult.getBlockPos().offset(hitResult.getSide()), null);
    }

    playEmptyingSound(player, world, pos);
    return true;
  }

  @Override
  public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
    return Items.BAT_SPAWN_EGG;
  }

  @Override
  public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
    if (PolymerResourcePackUtils.hasMainPack(context)) {
      return Identifier.of("greenpanda", "bat_bucket");
    } else {
      return Identifier.of("minecraft", "bat_spawn_egg");
    }
  }
}