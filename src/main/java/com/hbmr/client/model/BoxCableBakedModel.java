package com.hbmr.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.hbmr.block.network.BoxDuctBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;

/**
 * Chunk-meshed box duct matching 1.7.10 {@code RenderBoxDuct}
 * ({@code PowerCableBox} / {@code FluidDuctBox}).
 */
public final class BoxCableBakedModel implements IDynamicBakedModel {
	private static final ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());

	private final int size;
	private final boolean fluidHub;
	private final boolean pneumatic;
	private final TextureAtlasSprite straight;
	private final TextureAtlasSprite junction;
	private final TextureAtlasSprite curveTl;
	private final TextureAtlasSprite curveTr;
	private final TextureAtlasSprite curveBl;
	private final TextureAtlasSprite curveBr;
	private final TextureAtlasSprite end;
	private final TextureAtlasSprite particle;
	private final ItemTransforms transforms;
	private final boolean useBlockLight;
	private final List<BakedQuad> inventoryQuads;
	private final Map<Integer, List<BakedQuad>> worldCache = new ConcurrentHashMap<>();

	public BoxCableBakedModel(int size, boolean fluidHub, boolean pneumatic, TextureAtlasSprite straight,
			TextureAtlasSprite junction, TextureAtlasSprite curveTl, TextureAtlasSprite curveTr,
			TextureAtlasSprite curveBl, TextureAtlasSprite curveBr, TextureAtlasSprite end,
			TextureAtlasSprite particle, ItemTransforms transforms, boolean useBlockLight) {
		this.size = size;
		this.fluidHub = fluidHub;
		this.pneumatic = pneumatic;
		this.straight = straight;
		this.junction = junction;
		this.curveTl = curveTl;
		this.curveTr = curveTr;
		this.curveBl = curveBl;
		this.curveBr = curveBr;
		this.end = end;
		this.particle = particle;
		this.transforms = transforms;
		this.useBlockLight = useBlockLight;
		this.inventoryQuads = buildInventory();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
			ModelData data, @Nullable RenderType renderType) {
		if (side != null) {
			return Collections.emptyList();
		}
		if (state == null || !(state.getBlock() instanceof BoxDuctBlock)) {
			return inventoryQuads;
		}
		boolean pX = state.getValue(BoxDuctBlock.EAST);
		boolean nX = state.getValue(BoxDuctBlock.WEST);
		boolean pY = state.getValue(BoxDuctBlock.UP);
		boolean nY = state.getValue(BoxDuctBlock.DOWN);
		boolean pZ = state.getValue(BoxDuctBlock.SOUTH);
		boolean nZ = state.getValue(BoxDuctBlock.NORTH);
		int mask = (pX ? 32 : 0) + (nX ? 16 : 0) + (pY ? 8 : 0) + (nY ? 4 : 0) + (pZ ? 2 : 0) + (nZ ? 1 : 0);
		return worldCache.computeIfAbsent(mask, m -> buildWorld(pX, nX, pY, nY, pZ, nZ, m));
	}

	private List<BakedQuad> buildInventory() {
		float lower = (float) BoxDuctBlock.lowerForSize(size);
		float upper = 1.0F - lower;
		List<BakedQuad> quads = new ArrayList<>();
		if (pneumatic) {
			// X-run reads as a proper pipe under the standard GUI [30,225] transform;
			// Z-run foreshortens into a stubby end-face (1.7 inventory used Z under a
			// different ISBRH camera). Ends still use connector like RenderPneumoTube.
			FaceIcons icons = new FaceIcons(straight, straight, straight, straight, end, end);
			addBox(quads, 0.0F, lower, lower, 1.0F, upper, upper, icons, UvRots.none());
		} else {
			FaceIcons icons = FaceIcons.inventory(end, straight);
			addBox(quads, lower, lower, 0.0F, upper, upper, 1.0F, icons, UvRots.straightZ());
		}
		return List.copyOf(quads);
	}

	private List<BakedQuad> buildWorld(boolean pX, boolean nX, boolean pY, boolean nY, boolean pZ, boolean nZ,
			int mask) {
		if (pneumatic) {
			return buildPneumaticWorld(pX, nX, pY, nY, pZ, nZ, mask);
		}
		int count = (pX ? 1 : 0) + (nX ? 1 : 0) + (pY ? 1 : 0) + (nY ? 1 : 0) + (pZ ? 1 : 0) + (nZ ? 1 : 0);
		float lower = (float) BoxDuctBlock.lowerForSize(size);
		float upper = 1.0F - lower;
		FaceIcons icons = FaceIcons.from(this, mask, count, pX, nX, pY, nY, pZ, nZ);
		UvRots rots = UvRots.from(false, mask, count, pX, nX, pY, nY);
		List<BakedQuad> quads = new ArrayList<>();

		if ((mask & 0b001111) == 0 && mask > 0) {
			addBox(quads, 0.0F, lower, lower, 1.0F, upper, upper, icons, rots);
		} else if ((mask & 0b111100) == 0 && mask > 0) {
			addBox(quads, lower, lower, 0.0F, upper, upper, 1.0F, icons, rots);
		} else if ((mask & 0b110011) == 0 && mask > 0) {
			addBox(quads, lower, 0.0F, lower, upper, 1.0F, upper, icons, rots);
		} else if (count == 2) {
			addBox(quads, lower, lower, lower, upper, upper, upper, icons, rots);
			if (nY) {
				addBox(quads, lower, 0.0F, lower, upper, lower, upper, icons, rots);
			}
			if (pY) {
				addBox(quads, lower, upper, lower, upper, 1.0F, upper, icons, rots);
			}
			if (nX) {
				addBox(quads, 0.0F, lower, lower, lower, upper, upper, icons, rots);
			}
			if (pX) {
				addBox(quads, upper, lower, lower, 1.0F, upper, upper, icons, rots);
			}
			if (nZ) {
				addBox(quads, lower, lower, 0.0F, upper, upper, lower, icons, rots);
			}
			if (pZ) {
				addBox(quads, lower, lower, upper, upper, upper, 1.0F, icons, rots);
			}
		} else {
			float jLower = fluidHub
					? (float) BoxDuctBlock.junctionLowerForSize(size, BoxDuctBlock.NetworkKind.FLUID)
					: lower;
			float jUpper = 1.0F - jLower;
			addBox(quads, jLower, jLower, jLower, jUpper, jUpper, jUpper, icons, rots);
			if (nY) {
				addBox(quads, lower, 0.0F, lower, upper, jLower, upper, icons, rots);
			}
			if (pY) {
				addBox(quads, lower, jUpper, lower, upper, 1.0F, upper, icons, rots);
			}
			if (nX) {
				addBox(quads, 0.0F, lower, lower, jLower, upper, upper, icons, rots);
			}
			if (pX) {
				addBox(quads, jUpper, lower, lower, 1.0F, upper, upper, icons, rots);
			}
			if (nZ) {
				addBox(quads, lower, lower, 0.0F, upper, upper, jLower, icons, rots);
			}
			if (pZ) {
				addBox(quads, lower, lower, jUpper, upper, upper, 1.0F, icons, rots);
			}
		}
		return List.copyOf(quads);
	}

	/**
	 * Matches 1.7.10 {@code RenderPneumoTube} for normal (non-compressor / non-endpoint) tubes:
	 * through-runs are straight casing; everything else is hub+arms with {@code baseIcon}
	 * (junction). {@code renderCon} protrusions are NOT used — in 1.7 they only run when
	 * {@code canConnectToAir} / insertion / ejection dirs apply.
	 * Inventory still uses flat connector faces via {@link #buildInventory()}.
	 */
	private List<BakedQuad> buildPneumaticWorld(boolean pX, boolean nX, boolean pY, boolean nY, boolean pZ,
			boolean nZ, int mask) {
		int count = (pX ? 1 : 0) + (nX ? 1 : 0) + (pY ? 1 : 0) + (nY ? 1 : 0) + (pZ ? 1 : 0) + (nZ ? 1 : 0);
		float lower = (float) BoxDuctBlock.lowerForSize(size);
		float upper = 1.0F - lower;
		UvRots rots = UvRots.from(true, mask, count, pX, nX, pY, nY);
		FaceIcons straightIcons = FaceIcons.all(straight);
		FaceIcons hubIcons = FaceIcons.all(junction);
		List<BakedQuad> quads = new ArrayList<>();

		if (mask == 0b110000) {
			addBox(quads, 0.0F, lower, lower, 1.0F, upper, upper, straightIcons, rots,
					true, true, true, true, false, false);
		} else if (mask == 0b000011) {
			addBox(quads, lower, lower, 0.0F, upper, upper, 1.0F, straightIcons, rots,
					true, true, false, false, true, true);
		} else if (mask == 0b001100) {
			addBox(quads, lower, 0.0F, lower, upper, 1.0F, upper, straightIcons, rots,
					false, false, true, true, true, true);
		} else {
			// 1.7 "Any" path — dead ends, corners, T/X junctions, isolated hub.
			UvRots none = UvRots.none();
			addBox(quads, lower, lower, lower, upper, upper, upper, hubIcons, none,
					!nY, !pY, !nZ, !pZ, !nX, !pX);
			if (nY) {
				addBox(quads, lower, 0.0F, lower, upper, lower, upper, hubIcons, none,
						false, false, true, true, true, true);
			}
			if (pY) {
				addBox(quads, lower, upper, lower, upper, 1.0F, upper, hubIcons, none,
						false, false, true, true, true, true);
			}
			if (nX) {
				addBox(quads, 0.0F, lower, lower, lower, upper, upper, hubIcons, none,
						true, true, true, true, false, false);
			}
			if (pX) {
				addBox(quads, upper, lower, lower, 1.0F, upper, upper, hubIcons, none,
						true, true, true, true, false, false);
			}
			if (nZ) {
				addBox(quads, lower, lower, 0.0F, upper, upper, lower, hubIcons, none,
						true, true, false, false, true, true);
			}
			if (pZ) {
				addBox(quads, lower, lower, upper, upper, upper, 1.0F, hubIcons, none,
						true, true, false, false, true, true);
			}
		}
		return List.copyOf(quads);
	}

	/** Emit all six faces. End-cap sprites always use rot 0 — curve {@code uvRotate*} is for
	 * casing only; applying it to {@code end} turns the concentric core into horizontal stripes. */
	private void addBox(List<BakedQuad> quads, float x0, float y0, float z0, float x1, float y1, float z1,
			FaceIcons icons, UvRots rots) {
		addBox(quads, x0, y0, z0, x1, y1, z1, icons, rots, true, true, true, true, true, true);
	}

	private void addBox(List<BakedQuad> quads, float x0, float y0, float z0, float x1, float y1, float z1,
			FaceIcons icons, UvRots rots, boolean down, boolean up, boolean north, boolean south, boolean west,
			boolean east) {
		if (down) {
			faceY(quads, icons.down(), rotFor(icons.down(), rots.down()), false, x0, x1, y0, z0, z1);
		}
		if (up) {
			faceY(quads, icons.up(), rotFor(icons.up(), rots.up()), true, x0, x1, y1, z0, z1);
		}
		if (north) {
			faceZ(quads, icons.north(), rotFor(icons.north(), rots.north()), false, x0, x1, y0, y1, z0);
		}
		if (south) {
			faceZ(quads, icons.south(), rotFor(icons.south(), rots.south()), true, x0, x1, y0, y1, z1);
		}
		if (west) {
			faceX(quads, icons.west(), rotFor(icons.west(), rots.west()), false, y0, y1, z0, z1, x0);
		}
		if (east) {
			faceX(quads, icons.east(), rotFor(icons.east(), rots.east()), true, y0, y1, z0, z1, x1);
		}
	}

	/** End-cap and curve sprites keep rot 0 — {@code uvRotate*} is only for straight casing. */
	private int rotFor(TextureAtlasSprite sprite, int rot) {
		if (sprite == end || sprite == curveTl || sprite == curveTr || sprite == curveBl || sprite == curveBr) {
			return 0;
		}
		return rot;
	}

	private void faceY(List<BakedQuad> quads, TextureAtlasSprite sprite, int rot, boolean up,
			float x0, float x1, float y, float z0, float z1) {
		// Per-vertex UVs matching 1.7.10 RenderBlocks renderFaceYPos / YNeg (d3..d10).
		float u00; // minX,minZ
		float v00;
		float u10; // maxX,minZ
		float v10;
		float u11; // maxX,maxZ
		float v11;
		float u01; // minX,maxZ
		float v01;

		if (up) {
			if (rot == 1) {
				u00 = z0;
				v00 = 1.0F - x0;
				u10 = z0;
				v10 = 1.0F - x1;
				u11 = z1;
				v11 = 1.0F - x1;
				u01 = z1;
				v01 = 1.0F - x0;
			} else if (rot == 2) {
				u00 = 1.0F - z0;
				v00 = x1;
				u10 = 1.0F - z1;
				v10 = x1;
				u11 = 1.0F - z1;
				v11 = x0;
				u01 = 1.0F - z0;
				v01 = x0;
			} else if (rot == 3) {
				u00 = 1.0F - x0;
				v00 = 1.0F - z0;
				u10 = 1.0F - x1;
				v10 = 1.0F - z0;
				u11 = 1.0F - x1;
				v11 = 1.0F - z1;
				u01 = 1.0F - x0;
				v01 = 1.0F - z1;
			} else {
				u00 = x0;
				v00 = z0;
				u10 = x1;
				v10 = z0;
				u11 = x1;
				v11 = z1;
				u01 = x0;
				v01 = z1;
			}
			emit(quads, sprite, Direction.UP,
					x1, y, z1, u11, v11,
					x1, y, z0, u10, v10,
					x0, y, z0, u00, v00,
					x0, y, z1, u01, v01);
		} else {
			if (rot == 1) {
				// uvRotateBottom == 1 (different shuffle than top)
				u00 = 1.0F - z0;
				v00 = x1;
				u10 = 1.0F - z1;
				v10 = x1;
				u11 = 1.0F - z1;
				v11 = x0;
				u01 = 1.0F - z0;
				v01 = x0;
			} else if (rot == 2) {
				u00 = z0;
				v00 = 1.0F - x1;
				u10 = z1;
				v10 = 1.0F - x1;
				u11 = z1;
				v11 = 1.0F - x0;
				u01 = z0;
				v01 = 1.0F - x0;
			} else if (rot == 3) {
				u00 = 1.0F - x0;
				v00 = 1.0F - z0;
				u10 = 1.0F - x1;
				v10 = 1.0F - z0;
				u11 = 1.0F - x1;
				v11 = 1.0F - z1;
				u01 = 1.0F - x0;
				v01 = 1.0F - z1;
			} else {
				u00 = x0;
				v00 = z0;
				u10 = x1;
				v10 = z0;
				u11 = x1;
				v11 = z1;
				u01 = x0;
				v01 = z1;
			}
			emit(quads, sprite, Direction.DOWN,
					x0, y, z1, u01, v01,
					x0, y, z0, u00, v00,
					x1, y, z0, u10, v10,
					x1, y, z1, u11, v11);
		}
	}

	private void faceZ(List<BakedQuad> quads, TextureAtlasSprite sprite, int rot, boolean south,
			float x0, float x1, float y0, float y1, float z) {
		// 1.7 renderFaceZPos (uvRotateWest) / ZNeg (uvRotateEast)
		float u00; // minX,minY
		float v00;
		float u10; // maxX,minY
		float v10;
		float u11; // maxX,maxY
		float v11;
		float u01; // minX,maxY
		float v01;

		if (rot == 1) {
			u00 = y0;
			v00 = 1.0F - x0;
			u10 = y1;
			v10 = 1.0F - x0;
			u11 = y1;
			v11 = 1.0F - x1;
			u01 = y0;
			v01 = 1.0F - x1;
			// 1.7 west/east rot 1: after shuffle V tracks X, U tracks Y with flips
			// ZPos rot1: d3=U(y0), d4=U(y1), d5=V(1-x1), d6=V(1-x0), then swap d5/d6
			// → d5=V(1-x0), d6=V(1-x1); d7=U(y0), d8=U(y1), d9=V(1-x1), d10=V(1-x0)
			// verts: (minX,maxY)=(d3,d5)=(U(y0),V(1-x0)), (minX,minY)=(d8,d10)=(U(y1),V(1-x0)),
			//         (maxX,minY)=(d4,d6)=(U(y1),V(1-x1)), (maxX,maxY)=(d7,d9)=(U(y0),V(1-x1))
			u01 = y0;
			v01 = 1.0F - x0;
			u00 = y1;
			v00 = 1.0F - x0;
			u10 = y1;
			v10 = 1.0F - x1;
			u11 = y0;
			v11 = 1.0F - x1;
		} else if (rot == 2) {
			// Similar to 1.7 rot 2 shuffle
			u01 = 1.0F - y1;
			v01 = x0;
			u00 = 1.0F - y0;
			v00 = x0;
			u10 = 1.0F - y0;
			v10 = x1;
			u11 = 1.0F - y1;
			v11 = x1;
		} else if (rot == 3) {
			u01 = 1.0F - x0;
			v01 = y1;
			u00 = 1.0F - x0;
			v00 = y0;
			u10 = 1.0F - x1;
			v10 = y0;
			u11 = 1.0F - x1;
			v11 = y1;
		} else {
			// rot 0 — RenderBlocksNT mirrors U on ZNeg (north)
			if (south) {
				u00 = x0;
				u10 = x1;
				u11 = x1;
				u01 = x0;
			} else {
				u00 = 1.0F - x0;
				u10 = 1.0F - x1;
				u11 = 1.0F - x1;
				u01 = 1.0F - x0;
			}
			v00 = 1.0F - y0;
			v10 = 1.0F - y0;
			v11 = 1.0F - y1;
			v01 = 1.0F - y1;
		}

		if (south) {
			emit(quads, sprite, Direction.SOUTH,
					x0, y1, z, u01, v01,
					x0, y0, z, u00, v00,
					x1, y0, z, u10, v10,
					x1, y1, z, u11, v11);
		} else {
			emit(quads, sprite, Direction.NORTH,
					x1, y0, z, u10, v10,
					x0, y0, z, u00, v00,
					x0, y1, z, u01, v01,
					x1, y1, z, u11, v11);
		}
	}

	private void faceX(List<BakedQuad> quads, TextureAtlasSprite sprite, int rot, boolean east,
			float y0, float y1, float z0, float z1, float x) {
		float u00; // minZ,minY  — interpreted per face
		float v00;
		float u01; // maxZ,minY or as needed
		float v01;
		float u11;
		float v11;
		float u10;
		float v10;

		if (rot == 1) {
			// uvRotateNorth/South == 1 on X faces
			u00 = y0;
			v00 = 1.0F - z1;
			u10 = y1;
			v10 = 1.0F - z1;
			u11 = y1;
			v11 = 1.0F - z0;
			u01 = y0;
			v01 = 1.0F - z0;
			// After 1.7 XNeg rot1 shuffle:
			// verts (x, maxY, maxZ)=(d7,d9), (x,maxY,minZ)=(d3,d5), (x,minY,minZ)=(d8,d10), (x,minY,maxZ)=(d4,d6)
			u11 = y0;
			v11 = 1.0F - z0; // will remap in emit
			u10 = y0;
			v10 = 1.0F - z1;
			u00 = y1;
			v00 = 1.0F - z1;
			u01 = y1;
			v01 = 1.0F - z0;
		} else if (rot == 2) {
			u10 = 1.0F - y1;
			v10 = z0;
			u00 = 1.0F - y0;
			v00 = z0;
			u01 = 1.0F - y0;
			v01 = z1;
			u11 = 1.0F - y1;
			v11 = z1;
		} else if (rot == 3) {
			u10 = 1.0F - z0;
			v10 = y1;
			u00 = 1.0F - z0;
			v00 = y0;
			u01 = 1.0F - z1;
			v01 = y0;
			u11 = 1.0F - z1;
			v11 = y1;
		} else {
			// rot 0 — RenderBlocksNT mirrors U on XPos (east): U(16 - z)
			if (east) {
				u00 = 1.0F - z0;
				u01 = 1.0F - z1;
				u11 = 1.0F - z1;
				u10 = 1.0F - z0;
			} else {
				u00 = z0;
				u01 = z1;
				u11 = z1;
				u10 = z0;
			}
			v00 = 1.0F - y0;
			v01 = 1.0F - y0;
			v11 = 1.0F - y1;
			v10 = 1.0F - y1;
		}

		if (east) {
			emit(quads, sprite, Direction.EAST,
					x, y0, z1, u01, v01,
					x, y0, z0, u00, v00,
					x, y1, z0, u10, v10,
					x, y1, z1, u11, v11);
		} else {
			emit(quads, sprite, Direction.WEST,
					x, y1, z1, u11, v11,
					x, y1, z0, u10, v10,
					x, y0, z0, u00, v00,
					x, y0, z1, u01, v01);
		}
	}

	private static void emit(List<BakedQuad> quads, TextureAtlasSprite sprite, Direction dir,
			float x0, float y0, float z0, float u0, float v0,
			float x1, float y1, float z1, float u1, float v1,
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3) {
		QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer(quads::add);
		baker.setSprite(sprite);
		baker.setDirection(dir);
		baker.setShade(false);
		baker.setHasAmbientOcclusion(false);
		float nx = dir.getStepX();
		float ny = dir.getStepY();
		float nz = dir.getStepZ();
		vert(baker, sprite, x0, y0, z0, u0, v0, nx, ny, nz);
		vert(baker, sprite, x1, y1, z1, u1, v1, nx, ny, nz);
		vert(baker, sprite, x2, y2, z2, u2, v2, nx, ny, nz);
		vert(baker, sprite, x3, y3, z3, u3, v3, nx, ny, nz);
	}

	private static void vert(QuadBakingVertexConsumer baker, TextureAtlasSprite sprite,
			float x, float y, float z, float u, float v, float nx, float ny, float nz) {
		baker.vertex(x, y, z);
		baker.color(255, 255, 255, 255);
		baker.uv(sprite.getU(u * 16.0F), sprite.getV(v * 16.0F));
		baker.overlayCoords(0, 10);
		baker.uv2(0, 0);
		baker.normal(nx, ny, nz);
		baker.endVertex();
	}

	private record UvRots(int down, int up, int north, int south, int west, int east) {
		static UvRots none() {
			return new UvRots(0, 0, 0, 0, 0, 0);
		}

		/**
		 * Maps 1.7 {@code RenderBlocks.uvRotate*} fields onto faces.
		 * Vanilla: North→XNeg(west), South→XPos(east), East→ZNeg(north), West→ZPos(south).
		 */
		static UvRots straightZ() {
			// RenderBoxDuct: uvRotateNorth=1, uvRotateSouth=2 → west=1, east=2
			return new UvRots(0, 0, 0, 0, 1, 2);
		}

		static UvRots from(boolean pneumatic, int mask, int count, boolean pX, boolean nX, boolean pY,
				boolean nY) {
			if (pneumatic) {
				// 1.7.10 RenderPneumoTube — not RenderBoxDuct rotations.
				if ((mask & 0b001111) == 0 && mask > 0) {
					// Straight X: no uvRotate*
					return none();
				}
				if ((mask & 0b111100) == 0 && mask > 0) {
					// Straight Z: uvRotateTop=2, uvRotateBottom=1
					return new UvRots(1, 2, 0, 0, 0, 0);
				}
				if ((mask & 0b110011) == 0 && mask > 0) {
					// Straight Y: uvRotateNorth/South/East/West = 2
					// → west=2, east=2, north=2, south=2
					return new UvRots(0, 0, 2, 2, 2, 2);
				}
				return none();
			}
			if ((mask & 0b001111) == 0 && mask > 0) {
				// uvRotateTop/Bottom=1, uvRotateEast=2, uvRotateWest=1 → up/down=1, north=2, south=1
				return new UvRots(1, 1, 2, 1, 0, 0);
			}
			if ((mask & 0b111100) == 0 && mask > 0) {
				return straightZ();
			}
			if ((mask & 0b110011) == 0 && mask > 0) {
				return none();
			}
			if (count == 2) {
				int top = 0;
				int bottom = 0;
				int north = 0;
				int south = 0;
				int west = 0;
				int east = 0;
				if (nY && (pX || nX) || pY && (pX || nX)) {
					top = 1;
					bottom = 1;
				}
				if (!nY && !pY) {
					// uvRotateNorth=1, South=2, East=2, West=1 → west=1, east=2, north=2, south=1
					west = 1;
					east = 2;
					north = 2;
					south = 1;
				}
				return new UvRots(bottom, top, north, south, west, east);
			}
			return none();
		}
	}

	private record FaceIcons(
			TextureAtlasSprite down,
			TextureAtlasSprite up,
			TextureAtlasSprite north,
			TextureAtlasSprite south,
			TextureAtlasSprite west,
			TextureAtlasSprite east
	) {
		static FaceIcons inventory(TextureAtlasSprite end, TextureAtlasSprite straight) {
			return new FaceIcons(straight, straight, end, end, straight, straight);
		}

		static FaceIcons all(TextureAtlasSprite sprite) {
			return new FaceIcons(sprite, sprite, sprite, sprite, sprite, sprite);
		}

		/**
		 * Face textures for a cell. Straight runs put {@code end} only on <em>open</em> tips
		 * (same look as a lone inventory tube). Faces toward a neighbor use {@code straight}
		 * so shared segment joints don’t draw overlapping end-caps (1.20 doesn’t cull those
		 * non-opaque faces the way full cubes do).
		 */
		static FaceIcons from(BoxCableBakedModel m, int mask, int count,
				boolean pX, boolean nX, boolean pY, boolean nY, boolean pZ, boolean nZ) {
			TextureAtlasSprite[] faces = new TextureAtlasSprite[6];

			if ((mask & 0b001111) == 0 && mask > 0) {
				// Straight X — end caps only on open tips
				for (Direction d : Direction.values()) {
					faces[d.ordinal()] = switch (d) {
						case WEST -> nX ? m.straight : m.end;
						case EAST -> pX ? m.straight : m.end;
						default -> m.straight;
					};
				}
			} else if ((mask & 0b111100) == 0 && mask > 0) {
				// Straight Z
				for (Direction d : Direction.values()) {
					faces[d.ordinal()] = switch (d) {
						case NORTH -> nZ ? m.straight : m.end;
						case SOUTH -> pZ ? m.straight : m.end;
						default -> m.straight;
					};
				}
			} else if ((mask & 0b110011) == 0 && mask > 0) {
				// Straight Y
				for (Direction d : Direction.values()) {
					faces[d.ordinal()] = switch (d) {
						case DOWN -> nY ? m.straight : m.end;
						case UP -> pY ? m.straight : m.end;
						default -> m.straight;
					};
				}
			} else {
				for (Direction d : Direction.values()) {
					boolean towardConn = switch (d) {
						case DOWN -> nY;
						case UP -> pY;
						case NORTH -> nZ;
						case SOUTH -> pZ;
						case WEST -> nX;
						case EAST -> pX;
					};
					if (towardConn) {
						// Into a neighbor: casing, not an end-cap (avoids joint seams).
						faces[d.ordinal()] = m.straight;
						continue;
					}
					if (count == 2) {
						boolean oppositeConn = switch (d) {
							case UP -> nY;
							case DOWN -> pY;
							case SOUTH -> nZ;
							case NORTH -> pZ;
							case EAST -> nX;
							case WEST -> pX;
						};
						if (oppositeConn) {
							faces[d.ordinal()] = m.straight;
							continue;
						}
						TextureAtlasSprite curve = pickCurve(m, d, pX, nX, pY, nY, pZ, nZ);
						faces[d.ordinal()] = curve != null ? curve : m.junction;
					} else {
						// Isolated hub / multi-way junction casing.
						faces[d.ordinal()] = m.junction;
					}
				}
			}

			return new FaceIcons(
					faces[Direction.DOWN.ordinal()],
					faces[Direction.UP.ordinal()],
					faces[Direction.NORTH.ordinal()],
					faces[Direction.SOUTH.ordinal()],
					faces[Direction.WEST.ordinal()],
					faces[Direction.EAST.ordinal()]);
		}

		/** Matches 1.7.10 {@code FluidDuctBox} / {@code PowerCableBox#getIcon} curve table. */
		@Nullable
		private static TextureAtlasSprite pickCurve(BoxCableBakedModel m, Direction side,
				boolean pX, boolean nX, boolean pY, boolean nY, boolean pZ, boolean nZ) {
			if (nY && pZ) {
				return side == Direction.WEST ? m.curveBr : m.curveBl;
			}
			if (nY && nZ) {
				return side == Direction.EAST ? m.curveBr : m.curveBl;
			}
			if (nY && pX) {
				return side == Direction.SOUTH ? m.curveBr : m.curveBl;
			}
			if (nY && nX) {
				return side == Direction.NORTH ? m.curveBr : m.curveBl;
			}
			if (pY && pZ) {
				return side == Direction.WEST ? m.curveTr : m.curveTl;
			}
			if (pY && nZ) {
				return side == Direction.EAST ? m.curveTr : m.curveTl;
			}
			if (pY && pX) {
				return side == Direction.SOUTH ? m.curveTr : m.curveTl;
			}
			if (pY && nX) {
				return side == Direction.NORTH ? m.curveTr : m.curveTl;
			}
			if (pX && nZ) {
				return m.curveTr;
			}
			if (pX && pZ) {
				return m.curveBr;
			}
			if (nX && nZ) {
				return m.curveTl;
			}
			if (nX && pZ) {
				return m.curveBl;
			}
			return null;
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return useBlockLight;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return particle;
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	@Override
	@SuppressWarnings("deprecation")
	public ItemTransforms getTransforms() {
		return transforms;
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
		// Sprites are fully opaque (chamfer texels filled); solid avoids cutout punching sky holes
		// on elbow seams where those corners used to be alpha=0.
		return SOLID;
	}

	@Override
	public List<RenderType> getRenderTypes(net.minecraft.world.item.ItemStack itemStack, boolean fabulous) {
		return List.of(RenderType.solid());
	}

	@Override
	public List<BakedModel> getRenderPasses(net.minecraft.world.item.ItemStack itemStack, boolean fabulous) {
		return List.of(this);
	}
}
