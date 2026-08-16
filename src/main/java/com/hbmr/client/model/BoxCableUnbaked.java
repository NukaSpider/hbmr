package com.hbmr.client.model;

import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public final class BoxCableUnbaked implements IUnbakedGeometry<BoxCableUnbaked> {
	private final int size;
	/** When true, junction hub uses fluid {@code jLower} (slightly larger than arms). */
	private final boolean fluidHub;
	/** When true, UV rotations match 1.7.10 {@code RenderPneumoTube} (not {@code RenderBoxDuct}). */
	private final boolean pneumatic;

	public BoxCableUnbaked(int size, boolean fluidHub, boolean pneumatic) {
		this.size = size;
		this.fluidHub = fluidHub;
		this.pneumatic = pneumatic;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
			Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
			ItemOverrides overrides, ResourceLocation modelLocation) {
		TextureAtlasSprite straight = spriteGetter.apply(context.getMaterial("straight"));
		TextureAtlasSprite junction = spriteGetter.apply(context.getMaterial("junction"));
		TextureAtlasSprite curveTl = spriteGetter.apply(context.getMaterial("curve_tl"));
		TextureAtlasSprite curveTr = spriteGetter.apply(context.getMaterial("curve_tr"));
		TextureAtlasSprite curveBl = spriteGetter.apply(context.getMaterial("curve_bl"));
		TextureAtlasSprite curveBr = spriteGetter.apply(context.getMaterial("curve_br"));
		TextureAtlasSprite end = spriteGetter.apply(context.getMaterial("end"));
		TextureAtlasSprite particle = context.hasMaterial("particle")
				? spriteGetter.apply(context.getMaterial("particle"))
				: straight;
		return new BoxCableBakedModel(size, fluidHub, pneumatic, straight, junction, curveTl, curveTr, curveBl,
				curveBr, end, particle, context.getTransforms(), context.useBlockLight());
	}
}
