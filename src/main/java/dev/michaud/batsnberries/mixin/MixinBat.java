package dev.michaud.batsnberries.mixin;

import dev.michaud.batsnberries.gamerules.ModGameRules;
import dev.michaud.batsnberries.items.ModItems;
import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BatEntity.class)
public abstract class MixinBat extends AmbientEntity implements Bucketable {

  protected MixinBat(EntityType<? extends AmbientEntity> entityType, World world) {
    super(entityType, world);
  }

  @Shadow
  public abstract void setRoosting(boolean roosting);

  // -- LEASHABLE STUFF
  @Override
  public boolean canBeLeashed() {
    return true;
  }

  // -- BUCKETABLE STUFF
  @Override
  public ActionResult interactMob(PlayerEntity player, Hand hand) {

    ItemStack itemStack = player.getStackInHand(hand);

    if (itemStack.isOf(Items.GLOW_BERRIES)) {

      ItemStack itemStack2 = doEcholocation(itemStack);
      player.setStackInHand(hand, itemStack2);

      return ActionResult.SUCCESS;
    } else if (itemStack.isOf(Items.BUCKET)) {

      ItemStack newItem = fillBucket(player, hand);
      player.getItemCooldownManager().set(newItem, 5);

      return ActionResult.SUCCESS.withNewHandStack(newItem);
    }

    return super.interactMob(player, hand);
  }

  @Override
  public boolean isFromBucket() {
    //return dataTracker.get(FROM_BUCKET);
    return false;
  }

  @Override
  public void setFromBucket(boolean fromBucket) {
    // dataTracker.set(FROM_BUCKET, fromBucket);
  }

  @Override
  public void copyDataToStack(ItemStack stack) {
    stack.set(DataComponentTypes.CUSTOM_NAME, getCustomName());

    NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, nbtCompound -> {
      if (isAiDisabled()) {
        nbtCompound.putBoolean("NoAI", true);
      }

      if (isSilent()) {
        nbtCompound.putBoolean("Silent", true);
      }

      if (hasNoGravity()) {
        nbtCompound.putBoolean("NoGravity", true);
      }

      if (isGlowingLocal()) {
        nbtCompound.putBoolean("Glowing", true);
      }

      if (isInvulnerable()) {
        nbtCompound.putBoolean("Invulnerable", true);
      }

      nbtCompound.putFloat("Health", getHealth());
    });
  }

  @Override
  public void copyDataFromNbt(NbtCompound nbt) {
    nbt.getBoolean("NoAI").ifPresent(this::setAiDisabled);
    nbt.getBoolean("Silent").ifPresent(this::setSilent);
    nbt.getBoolean("NoGravity").ifPresent(this::setNoGravity);
    nbt.getBoolean("Invulnerable").ifPresent(this::setInvulnerable);
    nbt.getFloat("Health").ifPresent(this::setHealth);
    setRoosting(false);
  }

  @Override
  public ItemStack getBucketItem() {
    return new ItemStack(ModItems.BAT_BUCKET);
  }

  @Override
  public SoundEvent getBucketFillSound() {
    return SoundEvents.ITEM_BUCKET_FILL_AXOLOTL;
  }

  @Unique
  private ItemStack doEcholocation(ItemStack itemStack) {

    final BatEntity thisBat = (BatEntity) (Object) this;

    if (!(getWorld() instanceof ServerWorld world)) {
      return itemStack;
    }

    // Game rules
    final int sonarDistance = world.getGameRules()
        .getInt(ModGameRules.BAT_SONAR_MAX_DISTANCE);
    final int sonarDuration = world.getGameRules()
        .getInt(ModGameRules.BAT_SONAR_EFFECT_DURATION);

    // Eat berries
    ItemStack newItemStack = itemStack.finishUsing(world, thisBat);
    playSound(SoundEvents.ENTITY_ARMADILLO_EAT, 1f, 1f);

    // Add effect to others
    if (sonarDistance == 0 || sonarDuration == 0) {
      return newItemStack;
    }

    final Box searchBox = new Box(getBlockPos()).expand(sonarDistance);
    final List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class,
        searchBox);

    for (LivingEntity entity : entities) {

      if (isOccluded(world, thisBat.getPos(), entity.getPos())) {
        continue;
      }

      if (entity.getUuid() == thisBat.getUuid()) {
        continue;
      }

      StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.GLOWING, sonarDuration,
          0, true, true);
      entity.addStatusEffect(effect, thisBat);
    }

    return newItemStack;
  }

  /**
   * Check if the entity is blocked by wool or other vibration hiders
   */
  @Unique
  private boolean isOccluded(World world, Vec3d batPos, Vec3d entityPos) {

    Vec3d adjustedBatPos = new Vec3d(
        (double) MathHelper.floor(batPos.x) + 0.5,
        (double) MathHelper.floor(batPos.y) + 0.5,
        (double) MathHelper.floor(batPos.z) + 0.5
    );
    Vec3d adjustedEntityPos = new Vec3d(
        (double) MathHelper.floor(entityPos.x) + 0.5,
        (double) MathHelper.floor(entityPos.y) + 0.5,
        (double) MathHelper.floor(entityPos.z) + 0.5
    );

    for (Direction direction : Direction.values()) {
      Vec3d offsetPos = adjustedBatPos.offset(direction, 1.0E-5F);

      BlockStateRaycastContext context = new BlockStateRaycastContext(offsetPos, adjustedEntityPos,
          state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS));

      if (world.raycast(context).getType() != HitResult.Type.BLOCK) {
        return false;
      }
    }

    return true;
  }

  /**
   * Fill the bucket with this entity (and de-spawn)
   */
  @Unique
  private ItemStack fillBucket(PlayerEntity player, Hand hand) {

    playSound(getBucketFillSound(), 1f, 1f);

    ItemStack bucketItem = getBucketItem();
    copyDataToStack(bucketItem);

    ItemStack handItem = player.getStackInHand(hand);
    ItemStack newStackInHand = ItemUsage.exchangeStack(handItem, player, bucketItem, false);
    player.setStackInHand(hand, newStackInHand);

    if (!getWorld().isClient) {
      Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity) player, bucketItem);
    }

    discard();
    return newStackInHand;
  }

}