package com.hbmr.item;

import com.hbmr.client.RoRItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/** Block item that draws RoR panel + overlay OBJs in inventory (1.7.10 style). */
public class RoRBlockItem extends BlockItem {
	private final RoRKind kind;

	public RoRBlockItem(Block block, Properties properties, RoRKind kind) {
		super(block, properties);
		this.kind = kind;
	}

	public RoRKind getKind() {
		return kind;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return RoRItemRenderer.getInstance();
			}
		});
	}

	public enum RoRKind {
		BLANK,
		DISPLAY,
		KEYPAD,
		LEVER,
		GAUGE,
		INDICATOR,
		NUMERIC,
		GRAPH
	}
}
