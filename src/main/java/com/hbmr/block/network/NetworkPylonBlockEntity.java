package com.hbmr.block.network;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Marker BE so pylons/connectors use TESR-style OBJ rendering like 1.7.10. */
public class NetworkPylonBlockEntity extends BlockEntity {
	public NetworkPylonBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.NETWORK_PYLON.get(), pos, state);
	}

	public NetworkPylonKind kind() {
		if (getBlockState().getBlock() instanceof NetworkPylonBlock pylon) {
			return pylon.kind();
		}
		return NetworkPylonKind.PYLON;
	}

	@Override
	public AABB getRenderBoundingBox() {
		NetworkPylonKind kind = kind();
		double h = kind.renderHeight();
		if (kind == NetworkPylonKind.LARGE || kind == NetworkPylonKind.SUBSTATION) {
			return new AABB(
					worldPosition.getX() - 3.0D,
					worldPosition.getY(),
					worldPosition.getZ() - 4.0D,
					worldPosition.getX() + 4.0D,
					worldPosition.getY() + h,
					worldPosition.getZ() + 4.0D);
		}
		return new AABB(
				worldPosition.getX() - 0.5D,
				worldPosition.getY(),
				worldPosition.getZ() - 1.0D,
				worldPosition.getX() + 1.5D,
				worldPosition.getY() + h,
				worldPosition.getZ() + 2.5D);
	}
}
