package com.hbmr.client;

import com.hbmr.HBMR;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Client key mappings for HBMR.
 * Bound and visible in Controls; behavior is wired later when systems need them.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HBMRKeybinds {

	public static final String CATEGORY = "key.categories.hbmr";

	public static final KeyMapping CALCULATOR = key("calculator", GLFW.GLFW_KEY_N);
	public static final KeyMapping ABILITY_ALT = key("ability_alt", GLFW.GLFW_KEY_LEFT_ALT);
	public static final KeyMapping COPY_TOOL_CTRL = key("copy_tool_ctrl", GLFW.GLFW_KEY_LEFT_CONTROL);
	public static final KeyMapping COPY_TOOL_ALT = key("copy_tool_alt", GLFW.GLFW_KEY_LEFT_ALT);
	public static final KeyMapping ABILITY_CYCLE = mouse("ability_cycle", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	public static final KeyMapping DASH = key("dash", GLFW.GLFW_KEY_LEFT_SHIFT);
	public static final KeyMapping GUN_TERTIARY = mouse("gun_tertiary", GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
	public static final KeyMapping CRANE_LOAD = key("crane_load", GLFW.GLFW_KEY_ENTER);
	public static final KeyMapping CRANE_DOWN = key("crane_down", GLFW.GLFW_KEY_DOWN);
	public static final KeyMapping CRANE_UP = key("crane_up", GLFW.GLFW_KEY_UP);
	public static final KeyMapping CRANE_LEFT = key("crane_left", GLFW.GLFW_KEY_LEFT);
	public static final KeyMapping CRANE_RIGHT = key("crane_right", GLFW.GLFW_KEY_RIGHT);
	public static final KeyMapping QMAW = key("qmaw", GLFW.GLFW_KEY_F1);
	public static final KeyMapping GUN_PRIMARY = mouse("gun_primary", GLFW.GLFW_MOUSE_BUTTON_LEFT);
	public static final KeyMapping RELOAD = key("reload", GLFW.GLFW_KEY_R);
	public static final KeyMapping GUN_SECONDARY = mouse("gun_secondary", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	public static final KeyMapping TOGGLE_HUD = key("toggle_hud", GLFW.GLFW_KEY_V);
	public static final KeyMapping TOGGLE_JETPACK = key("toggle_jetpack", GLFW.GLFW_KEY_C);
	public static final KeyMapping TOGGLE_MAGNET = key("toggle_magnet", GLFW.GLFW_KEY_Z);
	public static final KeyMapping TRAIN_INVENTORY = key("train_inventory", GLFW.GLFW_KEY_R);

	private HBMRKeybinds() {}

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		event.register(CALCULATOR);
		event.register(ABILITY_ALT);
		event.register(COPY_TOOL_CTRL);
		event.register(COPY_TOOL_ALT);
		event.register(ABILITY_CYCLE);
		event.register(DASH);
		event.register(GUN_TERTIARY);
		event.register(CRANE_LOAD);
		event.register(CRANE_DOWN);
		event.register(CRANE_UP);
		event.register(CRANE_LEFT);
		event.register(CRANE_RIGHT);
		event.register(QMAW);
		event.register(GUN_PRIMARY);
		event.register(RELOAD);
		event.register(GUN_SECONDARY);
		event.register(TOGGLE_HUD);
		event.register(TOGGLE_JETPACK);
		event.register(TOGGLE_MAGNET);
		event.register(TRAIN_INVENTORY);
	}

	private static KeyMapping key(String name, int glfwKey) {
		return new KeyMapping(
				"key.hbmr." + name,
				KeyConflictContext.IN_GAME,
				InputConstants.Type.KEYSYM.getOrCreate(glfwKey),
				CATEGORY
		);
	}

	private static KeyMapping mouse(String name, int glfwButton) {
		return new KeyMapping(
				"key.hbmr." + name,
				KeyConflictContext.IN_GAME,
				InputConstants.Type.MOUSE.getOrCreate(glfwButton),
				CATEGORY
		);
	}
}
