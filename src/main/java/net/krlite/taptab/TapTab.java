package net.krlite.taptab;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapTab implements ModInitializer {
	public static final String NAME = "Tap Tab", ID = "tap_tab";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final int TAB_DELAY = 400, ANIMATION_DURATION = 375, ANIMATION_AMOUNT = 35, ANIMATION_DELAY = 15;

	public static final KeyBinding CYCLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.tap_tab.cycle",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_TAB,
			"key.tap_tab.category"
	));

	public static class Input {
		private static long lastPressed;

		static void listenInput(MinecraftClient client) {
			if (client.player == null) return;

			if (CYCLE.wasPressed()) {
				if (System.currentTimeMillis() - lastPressed < TAB_DELAY) {
					if (client.options.sneakKey.isPressed())
						InventorySwapper.swapToPrevLine();
					else InventorySwapper.swapToNextLine();
				}

				lastPressed = System.currentTimeMillis();
			}
		}
	}

	public static class Sounds {
		public static SoundEvent SWAP_PREV = SoundEvent.of(new Identifier(ID, "swap_next"));
		public static SoundEvent SWAP_NEXT = SoundEvent.of(new Identifier(ID, "swap_prev"));

		static void register() {
			Registry.register(Registries.SOUND_EVENT, SWAP_PREV.getId(), SWAP_PREV);
			Registry.register(Registries.SOUND_EVENT, SWAP_NEXT.getId(), SWAP_NEXT);
		}
	}

	@Override
	public void onInitialize() {
		ClientTickEvents.START_CLIENT_TICK.register(Input::listenInput);
		Sounds.register();
	}

	private static double easeOutBounceProgress(double progress) {
		if (progress < 1 / 2.75) {
			return 7.5625 * progress * progress;
		} else if (progress < 2 / 2.75) {
			progress -= 1.5 / 2.75;
			return 7.5625 * progress * progress + 0.75;
		} else if (progress < 2.5 / 2.75) {
			progress -= 2.25 / 2.75;
			return 7.5625 * progress * progress + 0.9375;
		} else {
			progress -= 2.625 / 2.75;
			return 7.5625 * progress * progress + 0.984375;
		}
	}

	public static double easeOutBounce(double progress, boolean reversed) {
		return (easeOutBounceProgress(progress) - 1) * (reversed ? -1 : 1) * ANIMATION_AMOUNT;
	}
}
