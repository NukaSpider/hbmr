package com.hbmr.registry;

import com.hbmr.HBMR;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
			DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HBMR.MODID);

	public static final RegistryObject<SoundEvent> REACTOR_START = register("block.reactor_start");
	public static final RegistryObject<SoundEvent> REACTOR_STOP = register("block.reactor_stop");

	public static final RegistryObject<SoundEvent> SLIDING_DOOR_OPENING = register("door.sliding_door_opening");
	public static final RegistryObject<SoundEvent> SLIDING_DOOR_OPENED = register("door.sliding_door_opened");
	public static final RegistryObject<SoundEvent> SLIDING_DOOR_SHUT = register("door.sliding_door_shut");

	public static final RegistryObject<SoundEvent> WGH_START = register("door.wgh_start");
	public static final RegistryObject<SoundEvent> WGH_STOP = register("door.wgh_stop");
	public static final RegistryObject<SoundEvent> WGH_BIG_START = register("door.wgh_big_start");
	public static final RegistryObject<SoundEvent> WGH_BIG_STOP = register("door.wgh_big_stop");
	public static final RegistryObject<SoundEvent> DOOR_ALARM6 = register("door.alarm6");
	public static final RegistryObject<SoundEvent> GARAGE_MOVE = register("door.garage_move");
	public static final RegistryObject<SoundEvent> GARAGE_STOP = register("door.garage_stop");

	public static final RegistryObject<SoundEvent> QE_SLIDING_OPENING = register("door.qe_sliding_opening");
	public static final RegistryObject<SoundEvent> QE_SLIDING_OPENED = register("door.qe_sliding_opened");
	public static final RegistryObject<SoundEvent> QE_SLIDING_SHUT = register("door.qe_sliding_shut");

	public static final RegistryObject<SoundEvent> SLIDING_SEAL_OPEN = register("door.sliding_seal_open");
	public static final RegistryObject<SoundEvent> SLIDING_SEAL_STOP = register("door.sliding_seal_stop");

	public static final RegistryObject<SoundEvent> DOOR_LEVER = register("door.lever");

	public static final RegistryObject<SoundEvent> VAULT_SCRAPE = register("block.vault_scrape");
	public static final RegistryObject<SoundEvent> VAULT_THUD = register("block.vault_thud");
	public static final RegistryObject<SoundEvent> TRANSITION_SEAL_OPEN = register("door.transition_seal_open");

	/** 1.7.10 {@code hbm:block.pipePlaced} — fluid ducts / exhaust / pipe anchor place & break. */
	public static final RegistryObject<SoundEvent> PIPE_PLACED = register("block.pipe_placed");

	private static RegistryObject<SoundEvent> register(String name) {
		return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
				ResourceLocation.fromNamespaceAndPath(HBMR.MODID, name)));
	}

	private ModSounds() {
	}
}
