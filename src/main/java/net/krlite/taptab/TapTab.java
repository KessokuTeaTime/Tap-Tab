package net.krlite.taptab;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapTab implements ModInitializer {
	public static final String NAME = "Tap Tab", ID = "taptab";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final int TAB_DELAY = 400, ANIMATION_DURATION = 375, ANIMATION_AMOUNT = 35, ANIMATION_DELAY = 15;

	public static final KeyBinding CYCLE = KeyBindingHelper.registerKeyBinding(new TooltipKeyBinding(
			"key." + ID + ".cycle",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"key." + ID + ".category",
			() -> Tooltip.of(Text.translatable(
					"key." + ID + ".unbound.tooltip",
					Text.translatable(MinecraftClient.getInstance().options.playerListKey.getTranslationKey())
			))
	));
	public static final KeyBinding REVERSE_MODIFIER = KeyBindingHelper.registerKeyBinding(new TooltipKeyBinding(
			"key." + ID + ".reverse_modifier",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"key." + ID + ".category",
			() -> Tooltip.of(Text.translatable(
					"key." + ID + ".unbound.tooltip",
					Text.translatable(MinecraftClient.getInstance().options.sneakKey.getTranslationKey())
			))
	));
	public static final KeyBinding SLOT_MODIFIER = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key." + ID + ".slot_modifier",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_ALT,
			"key." + ID + ".category"
	));

	public static KeyBinding keyOrDefault(KeyBinding key, KeyBinding fallback) {
		return key.isUnbound() ? fallback : key;
	}

	public static class Input {
		private static long lastPressed;

		static void listenInput(MinecraftClient client) {
			if (client.player == null) return;

			if (keyOrDefault(CYCLE, client.options.playerListKey).wasPressed()) {
				if (System.currentTimeMillis() - lastPressed < TAB_DELAY) {
					boolean reverseModifier = keyOrDefault(REVERSE_MODIFIER, client.options.sneakKey).isPressed();
					boolean slotModifier = SLOT_MODIFIER.isPressed();

					if (slotModifier) {
						int slot = client.player.getInventory().selectedSlot;
						if (reverseModifier) InventorySwapper.swapSlotToPrevLine(slot);
						else InventorySwapper.swapSlotToNextLine(slot);
					} else {
						if (reverseModifier) InventorySwapper.swapToPrevLine();
						else InventorySwapper.swapToNextLine();
					}
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
