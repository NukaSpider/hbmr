package com.hbmr.block.network;

import net.minecraft.util.StringRepresentable;

public enum BoxDuctMaterial implements StringRepresentable {
	SILVER("silver"),
	COPPER("copper"),
	WHITE("white");

	private final String name;

	BoxDuctMaterial(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
