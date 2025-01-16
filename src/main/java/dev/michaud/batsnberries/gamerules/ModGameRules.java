package dev.michaud.batsnberries.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;

public class ModGameRules {

  public static final GameRules.Key<GameRules.IntRule> BAT_SONAR_MAX_DISTANCE = GameRuleRegistry
      .register("batSonarMaxDistance", Category.MOBS,
          GameRuleFactory.createIntRule(32, 0, Integer.MAX_VALUE));

  public static final GameRules.Key<GameRules.IntRule> BAT_SONAR_EFFECT_DURATION = GameRuleRegistry
      .register("batSonarEffectDuration", Category.MOBS,
          GameRuleFactory.createIntRule(140, 0, Integer.MAX_VALUE));

  public static final GameRules.Key<GameRules.IntRule> GLOW_BERRY_EFFECT_DURATION = GameRuleRegistry
      .register("glowBerryEffectDuration", Category.PLAYER,
          GameRuleFactory.createIntRule(200, 0, Integer.MAX_VALUE));

  public static void registerGameRules() {

  }

}