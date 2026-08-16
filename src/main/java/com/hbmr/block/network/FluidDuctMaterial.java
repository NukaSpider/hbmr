package com.hbmr.block.network;

import net.minecraft.util.StringRepresentable;

public enum FluidDuctMaterial implements StringRepresentable {
	NEO("neo"),
	SILVER("silver"),
	COLORED("colored"),
	PNEUMATIC("pneumatic");

	private final String name;

	FluidDuctMaterial(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
