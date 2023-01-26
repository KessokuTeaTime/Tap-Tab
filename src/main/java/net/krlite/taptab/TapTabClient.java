package net.krlite.taptab;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.krlite.equator.util.Timer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapTabClient implements ClientModInitializer {
	public static final String MOD_ID = "taptab";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int TAB_DELAY = 400, ANIMATION_DURATION = 375, ANIMATION_AMOUNT = 9, ANIMATION_DELAY = 15;

	public static class KeyBinds {
		private static final Timer lastPressed = new Timer(TAB_DELAY);

		static void listenInput(MinecraftClient client) {
			if (client.player == null) return;
			if (client.options.playerListKey.wasPressed()) {
				if (lastPressed.isPresent()) {
					lastPressed.reset();
					if (client.options.sneakKey.isPressed())
						InventorySwapper.swapToPrevLine();
					else InventorySwapper.swapToNextLine();
				} else lastPressed.reset();
			}
		}
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(KeyBinds::listenInput);
	}
}
