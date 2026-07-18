package band.kessokuteatime.taptab;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public final class TapTab {
	public static final String NAME = "Tap Tab";
	public static final String ID = "taptab";
	public static final int ANIMATION_DURATION = 375;
	public static final int ANIMATION_AMOUNT = 35;
	public static final int ANIMATION_DELAY = 15;
	public static final Identifier KEY_CATEGORY_ID = id("category");
	public static final Identifier SWAP_PREV_ID = id("swap_prev");
	public static final Identifier SWAP_NEXT_ID = id("swap_next");
	public static final SoundEvent SWAP_PREV = SoundEvent.createVariableRangeEvent(SWAP_PREV_ID);
	public static final SoundEvent SWAP_NEXT = SoundEvent.createVariableRangeEvent(SWAP_NEXT_ID);

	private static final long TAB_DELAY = 400;

	private static KeyMapping cycleKey;
	private static KeyMapping reverseModifierKey;
	private static KeyMapping slotModifierKey;
	private static long lastPressed;

	private TapTab() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

	public static KeyMapping createCycleKey(KeyMapping.Category category) {
		return new TooltipKeyMapping(
				"key.taptab.cycle",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				category,
				() -> fallbackTooltip(Minecraft.getInstance().options.keyPlayerList)
		);
	}

	public static KeyMapping createReverseModifierKey(KeyMapping.Category category) {
		return new TooltipKeyMapping(
				"key.taptab.reverse_modifier",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				category,
				() -> fallbackTooltip(Minecraft.getInstance().options.keyShift)
		);
	}

	public static KeyMapping createSlotModifierKey(KeyMapping.Category category) {
		return new KeyMapping(
				"key.taptab.slot_modifier",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT_ALT,
				category
		);
	}

	public static void setKeyMappings(KeyMapping cycle, KeyMapping reverseModifier, KeyMapping slotModifier) {
		cycleKey = cycle;
		reverseModifierKey = reverseModifier;
		slotModifierKey = slotModifier;
	}

	public static void tick(Minecraft minecraft) {
		if (minecraft.player == null || cycleKey == null
				|| reverseModifierKey == null || slotModifierKey == null) {
			return;
		}

		KeyMapping cycle = keyOrDefault(cycleKey, minecraft.options.keyPlayerList);
		if (!cycle.consumeClick()) {
			return;
		}

		long now = Util.getMillis();
		if (lastPressed != 0 && now - lastPressed < TAB_DELAY) {
			boolean reversed = keyOrDefault(reverseModifierKey, minecraft.options.keyShift).isDown();
			int selectedSlot = minecraft.player.getInventory().getSelectedSlot();

			if (slotModifierKey.isDown()) {
				if (reversed) {
					InventorySwapper.swapSlotToPreviousLine(selectedSlot);
				} else {
					InventorySwapper.swapSlotToNextLine(selectedSlot);
				}
			} else if (reversed) {
				InventorySwapper.swapToPreviousLine();
			} else {
				InventorySwapper.swapToNextLine();
			}
		}

		lastPressed = now;
	}

	public static double easeOutBounce(double progress, boolean reversed) {
		return (easeOutBounceProgress(progress) - 1) * (reversed ? -1 : 1) * ANIMATION_AMOUNT;
	}

	private static KeyMapping keyOrDefault(KeyMapping key, KeyMapping fallback) {
		return key.isUnbound() ? fallback : key;
	}

	private static Tooltip fallbackTooltip(KeyMapping fallback) {
		return Tooltip.create(Component.translatable(
				"key.taptab.unbound.tooltip",
				fallback.getTranslatedKeyMessage()
		));
	}

	private static double easeOutBounceProgress(double progress) {
		if (progress < 1 / 2.75) {
			return 7.5625 * progress * progress;
		}
		if (progress < 2 / 2.75) {
			progress -= 1.5 / 2.75;
			return 7.5625 * progress * progress + 0.75;
		}
		if (progress < 2.5 / 2.75) {
			progress -= 2.25 / 2.75;
			return 7.5625 * progress * progress + 0.9375;
		}

		progress -= 2.625 / 2.75;
		return 7.5625 * progress * progress + 0.984375;
	}
}
