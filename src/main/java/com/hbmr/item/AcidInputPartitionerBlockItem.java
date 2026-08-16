package com.hbmr.item;

import com.hbmr.client.AcidInputPartitionerItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/** Inventory mesh via {@code crane_buffer.obj} (animated belt). */
public class AcidInputPartitionerBlockItem extends BlockItem {
	public AcidInputPartitionerBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return AcidInputPartitionerItemRenderer.getInstance();
			}
		});
	}
}
