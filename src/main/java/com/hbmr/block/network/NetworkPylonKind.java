package com.hbmr.block.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.Block;

/**
 * Visual / collision variants for electricity pylons and connectors
 * (matches 1.7.10 {@code RenderPylon*} / {@code RenderConnector*}).
 * <p>
 * Pylon hitboxes are thin posts matching the shaft (~4px), one block tall —
 * height is covered by structure-dummy cells (1.7 Dummyable columns), not a
 * multi-block mega-AABB on the core. The small electricity pylon's core is a
 * full block (matches its chunky base mesh / 1.7 Dummyable cell).
 */
public enum NetworkPylonKind {
	CONNECTOR(
			"models/block/network/connector.obj",
			"textures/block/network/connector.png",
			null,
			false,
			Block.box(5.0D, 0.0D, 5.0D, 11.0D, 9.0D, 11.0D),
			1.0D,
			0),
	CONNECTOR_SUPER(
			"models/block/network/connector_super.obj",
			"textures/block/network/connector_super.png",
			null,
			false,
			Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D),
			1.0D,
			0),
	/** 1.7.10 {@code PylonRedWire} — Dummyable {4,0,0,0,0,0}; base cell is a full cube. */
	PYLON(
			"models/block/network/pylon.obj",
			"textures/block/network/pylon.png",
			"Pylon",
			false,
			Shapes.block(),
			6.0D,
			4),
	/** 1.7.10 {@code PylonMedium} — Dummyable {6,0,0,0,0,0}. */
	MEDIUM_WOOD(
			"models/block/network/pylon_medium.obj",
			"textures/block/network/pylon_medium.png",
			"Pylon",
			false,
			Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D),
			8.0D,
			6),
	MEDIUM_WOOD_TRANSFORMER(
			"models/block/network/pylon_medium.obj",
			"textures/block/network/pylon_medium.png",
			"Pylon",
			true,
			Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D),
			8.0D,
			6),
	MEDIUM_STEEL(
			"models/block/network/pylon_medium.obj",
			"textures/block/network/pylon_medium_steel.png",
			"Pylon",
			false,
			Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D),
			8.0D,
			6),
	MEDIUM_STEEL_TRANSFORMER(
			"models/block/network/pylon_medium.obj",
			"textures/block/network/pylon_medium_steel.png",
			"Pylon",
			true,
			Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D),
			8.0D,
			6),
	/** 1.7.10 {@code PylonLarge} — Dummyable {13,0,1,1,1,1}; full-block cells across 3×3×14. */
	LARGE(
			"models/block/network/pylon_large.obj",
			"textures/block/network/pylon_large.png",
			null,
			false,
			Shapes.block(),
			15.0D,
			13),
	/** 1.7.10 {@code Substation} — Dummyable {4,0,1,1,2,2}, placement offset 1. */
	SUBSTATION(
			"models/block/network/substation.obj",
			"textures/block/network/substation.png",
			null,
			false,
			Shapes.block(),
			6.0D,
			4);

	private final ResourceLocation model;
	private final ResourceLocation texture;
	/** When non-null, only this OBJ part is drawn (plus transformer if enabled). */
	private final String primaryPart;
	private final boolean transformer;
	private final VoxelShape shape;
	private final double renderHeight;
	/** Extra blocks above the core (1.7 Dummyable UP dimension). */
	private final int columnAbove;

	NetworkPylonKind(String modelPath, String texturePath, String primaryPart, boolean transformer,
			VoxelShape shape, double renderHeight, int columnAbove) {
		this.model = ResourceLocation.fromNamespaceAndPath("hbmr", modelPath);
		this.texture = ResourceLocation.fromNamespaceAndPath("hbmr", texturePath);
		this.primaryPart = primaryPart;
		this.transformer = transformer;
		this.shape = shape;
		this.renderHeight = renderHeight;
		this.columnAbove = columnAbove;
	}

	public ResourceLocation model() {
		return model;
	}

	public ResourceLocation texture() {
		return texture;
	}

	public String primaryPart() {
		return primaryPart;
	}

	public boolean hasTransformer() {
		return transformer;
	}

	public VoxelShape shape() {
		return shape;
	}

	/**
	 * Shape for vertical dummy cells above the core. The small pylon keeps a thin
	 * shaft up the column while its core ({@link #shape()}) is a full block.
	 */
	public VoxelShape columnShape() {
		if (this == PYLON) {
			return Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
		}
		return shape;
	}

	/**
	 * 1.7 Dummyable [U,D,N,S,W,E], or {@code null} for a simple vertical column of
	 * {@link #columnAbove()} cells.
	 */
	public int[] footprintDims() {
		return switch (this) {
			case LARGE -> new int[]{13, 0, 1, 1, 1, 1};
			case SUBSTATION -> new int[]{4, 0, 1, 1, 2, 2};
			default -> null;
		};
	}

	/** 1.7 {@code BlockDummyable#getOffset()} — core shifted back from the click along facing. */
	public int placementOffset() {
		return this == SUBSTATION ? 1 : 0;
	}

	public double renderHeight() {
		return renderHeight;
	}

	/** Blocks above the core that need thin shaft dummies (0 for connectors). */
	public int columnAbove() {
		return columnAbove;
	}

	public boolean isConnector() {
		return this == CONNECTOR || this == CONNECTOR_SUPER;
	}

	public boolean usesColumnDummies() {
		return columnAbove > 0;
	}
}
