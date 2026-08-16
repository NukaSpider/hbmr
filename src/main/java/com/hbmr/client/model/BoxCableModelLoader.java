package com.hbmr.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

/** Forge loader {@code hbmr:box_cable} — 1.7.10 {@code RenderBoxDuct} as a normal baked block. */
public final class BoxCableModelLoader implements IGeometryLoader<BoxCableUnbaked> {
	public static final BoxCableModelLoader INSTANCE = new BoxCableModelLoader();

	private BoxCableModelLoader() {
	}

	@Override
	public BoxCableUnbaked read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
		int size = json.has("size") ? json.get("size").getAsInt() : 0;
		if (size < 0 || size > 4) {
			throw new JsonParseException("box_cable size must be 0–4, got " + size);
		}
		boolean fluidHub = json.has("fluid") && json.get("fluid").getAsBoolean();
		boolean pneumatic = json.has("pneumatic") && json.get("pneumatic").getAsBoolean();
		return new BoxCableUnbaked(size, fluidHub, pneumatic);
	}
}
