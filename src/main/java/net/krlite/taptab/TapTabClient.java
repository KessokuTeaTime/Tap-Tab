package net.krlite.taptab;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.krlite.equator.util.Timer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapTabClient implements ClientModInitializer {
	public static final String MOD_ID = "taptab";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int TAB_DELAY = 400, ANIMATION_DURATION = 375, ANIMATION_AMOUNT = 35, ANIMATION_DELAY = 15;

	public static final KeyBinding CYCLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.taptab.cycle",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_TAB,
			"key.taptab.category"
	));

	public static class Input {
		private static final Timer lastPressed = new Timer(TAB_DELAY);

		static void listenInput(MinecraftClient client) {
			if (client.player == null) return;
			if (CYCLE.wasPressed()) {
				if (lastPressed.isPresent()) {
					lastPressed.reset();
					if (client.options.sneakKey.isPressed())
						InventorySwapper.swapToPrevLine();
					else InventorySwapper.swapToNextLine();
				} else lastPressed.reset();
			}
		}
	}

	public static class Sounds {
		public static SoundEvent SWAP_PREV = new SoundEvent(new Identifier(MOD_ID, "swap_next"));
		public static SoundEvent SWAP_NEXT = new SoundEvent(new Identifier(MOD_ID, "swap_prev"));

		static void register() {
			Registry.register(Registry.SOUND_EVENT, SWAP_PREV.getId(), SWAP_PREV);
			Registry.register(Registry.SOUND_EVENT, SWAP_NEXT.getId(), SWAP_NEXT);
		}
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(Input::listenInput);
		Sounds.register();
	}
}
