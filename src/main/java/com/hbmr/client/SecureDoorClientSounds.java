package com.hbmr.client;

import com.hbmr.block.multiblock.MultiblockControllerBlockEntity;
import com.hbmr.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Loops garage_move while the secure access door is opening/closing.
 */
@OnlyIn(Dist.CLIENT)
public final class SecureDoorClientSounds {
	private static LoopingDoorSound moveLoop;
	private static MultiblockControllerBlockEntity active;

	private SecureDoorClientSounds() {
	}

	public static void tick(MultiblockControllerBlockEntity be) {
		int state = be.getDoorState();
		boolean moving = state == MultiblockControllerBlockEntity.WIDE_OPENING
				|| state == MultiblockControllerBlockEntity.WIDE_CLOSING;

		if (!moving) {
			if (active == be) {
				stopAll();
			}
			return;
		}

		if (active != be) {
			stopAll();
			active = be;
		}

		Vec3 at = Vec3.atCenterOf(be.getBlockPos());
		moveLoop = ensurePlaying(moveLoop, ModSounds.GARAGE_MOVE.get(), at, 2.0F);
	}

	private static LoopingDoorSound ensurePlaying(LoopingDoorSound current, SoundEvent event, Vec3 at, float volume) {
		Minecraft mc = Minecraft.getInstance();
		if (current != null && mc.getSoundManager().isActive(current)) {
			current.setPos(at);
			return current;
		}
		LoopingDoorSound next = new LoopingDoorSound(event, at, volume);
		mc.getSoundManager().play(next);
		return next;
	}

	private static void stopAll() {
		Minecraft mc = Minecraft.getInstance();
		if (moveLoop != null) {
			mc.getSoundManager().stop(moveLoop);
			moveLoop = null;
		}
		active = null;
	}

	private static final class LoopingDoorSound extends AbstractTickableSoundInstance {
		private LoopingDoorSound(SoundEvent event, Vec3 at, float volume) {
			super(event, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
			this.looping = true;
			this.delay = 0;
			this.volume = volume;
			this.pitch = 1.0F;
			this.x = at.x;
			this.y = at.y;
			this.z = at.z;
			this.attenuation = Attenuation.LINEAR;
		}

		void setPos(Vec3 at) {
			this.x = at.x;
			this.y = at.y;
			this.z = at.z;
		}

		@Override
		public void tick() {
			if (active == null || active.isRemoved()) {
				stop();
			}
		}
	}
}
