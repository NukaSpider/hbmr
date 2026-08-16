package com.hbmr.client;

import com.hbmr.block.network.NetworkPylonBlock;
import com.hbmr.block.network.NetworkPylonKind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Inventory / hand renderer for electricity pylons and connectors. */
public final class NetworkPylonItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static NetworkPylonItemRenderer instance;

	public static NetworkPylonItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new NetworkPylonItemRenderer(mc);
		}
		return instance;
	}

	private NetworkPylonItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return;
		}
		Block block = blockItem.getBlock();
		if (!(block instanceof NetworkPylonBlock pylon)) {
			return;
		}
		NetworkPylonKind kind = pylon.kind();
		boolean gui = context == ItemDisplayContext.GUI || context == ItemDisplayContext.FIXED;
		poseStack.pushPose();
		// Center in the item cube — Y=0 left icons sitting on the slot floor.
		poseStack.translate(0.5D, 0.5D, 0.5D);
		NetworkPylonRenderer.renderInventory(poseStack, buffer, packedLight, packedOverlay, kind, gui);
		poseStack.popPose();
	}
}
