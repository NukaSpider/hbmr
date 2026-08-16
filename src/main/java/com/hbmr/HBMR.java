package com.hbmr;

import com.hbmr.registry.ModBlockEntities;
import com.hbmr.registry.ModBlocks;
import com.hbmr.registry.ModCreativeTabs;
import com.hbmr.registry.ModItems;
import com.hbmr.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * HBM's Nuclear Tech Mod - Re-Port
 * Forge 1.20.1 port entrypoint. Keep this class lean; feature registration lives in dedicated modules.
 */
@Mod(HBMR.MODID)
public class HBMR {
	public static final String MODID = "hbmr";
	public static final String NAME = "HBM's Nuclear Tech Mod - Re-Port";
	public static final Logger LOGGER = LogUtils.getLogger();

	public HBMR(FMLJavaModLoadingContext context) {
		IEventBus modBus = context.getModEventBus();
		ModBlocks.BLOCKS.register(modBus);
		ModItems.ITEMS.register(modBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modBus);
		ModCreativeTabs.CREATIVE_TABS.register(modBus);
		ModSounds.SOUND_EVENTS.register(modBus);
		modBus.addListener(this::commonSetup);
		LOGGER.info("{} ({}) initializing", NAME, MODID);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("{} common setup complete", NAME);
	}
}
