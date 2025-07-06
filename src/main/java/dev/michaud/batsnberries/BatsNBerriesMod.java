package dev.michaud.batsnberries;

import dev.michaud.batsnberries.events.handlers.AnimalEatGlowBerryHandler;
import dev.michaud.batsnberries.events.handlers.GlowBerryComponentModifyHandler;
import dev.michaud.batsnberries.gamerules.ModGameRules;
import dev.michaud.batsnberries.items.ModItems;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatsNBerriesMod implements ModInitializer {

  public static final String MOD_ID = "batsnberries"; //Must be the same as fabric.mod.json
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {

    GlowBerryComponentModifyHandler.initializeCallbacks(); /* Change glow berry components */
    AnimalEatGlowBerryHandler.initializeCallbacks();

    PolymerResourcePackUtils.addModAssets(MOD_ID);
    ModItems.registerModItems();
    ModGameRules.registerGameRules();

    LOGGER.info("Bats N' Berries Mod Initialized!");
  }
}