package com.hbmr.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

/**
 * Custom block sound types matching 1.7.10 {@code ModSoundTypes}.
 */
public final class ModSoundTypes {
	/**
	 * 1.7.10 {@code ModSoundTypes.pipe}: metal footsteps; place/break use
	 * {@code hbm:block.pipePlaced} at volume/pitch 0.85 (break slightly lower pitch).
	 */
	public static final SoundType PIPE = new ForgeSoundType(
			0.85F,
			0.85F,
			ModSounds.PIPE_PLACED,
			() -> SoundType.METAL.getStepSound(),
			ModSounds.PIPE_PLACED,
			() -> SoundType.METAL.getHitSound(),
			() -> SoundType.METAL.getFallSound());

	private ModSoundTypes() {
	}
}
