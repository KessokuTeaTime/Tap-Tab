package band.kessokuteatime.taptab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public final class InventorySwapper {
	public static final int TOP_LINE = 1;
	public static final int MIDDLE_LINE = 2;
	public static final int BOTTOM_LINE = 3;

	private static final long[] ANIMATION_START = new long[Inventory.getSelectionSize()];
	private static final boolean[] ANIMATION_REVERSED = new boolean[Inventory.getSelectionSize()];

	static {
		Arrays.fill(ANIMATION_START, -1);
	}

	private InventorySwapper() {
	}

	public static int animationOffset(int slot) {
		long start = ANIMATION_START[slot];
		if (start < 0) {
			return 0;
		}

		double progress = (double) Math.max(Util.getMillis() - start, 0) / TapTab.ANIMATION_DURATION;
		if (progress > 1) {
			ANIMATION_START[slot] = -1;
			return 0;
		}

		return (int) TapTab.easeOutBounce(progress, ANIMATION_REVERSED[slot]);
	}

	public static void swapToNextLine() {
		Arrays.fill(ANIMATION_REVERSED, false);
		swapLineWithHotbar(TOP_LINE);
		swapLineWithHotbar(MIDDLE_LINE);
		swapLineWithHotbar(BOTTOM_LINE);
		playSwapSound(false);
	}

	public static void swapToPreviousLine() {
		Arrays.fill(ANIMATION_REVERSED, true);
		swapLineWithHotbar(BOTTOM_LINE);
		swapLineWithHotbar(MIDDLE_LINE);
		swapLineWithHotbar(TOP_LINE);
		playSwapSound(true);
	}

	public static void swapSlotToNextLine(int slot) {
		ANIMATION_REVERSED[slot] = false;
		swapSlotWithHotbar(slot, TOP_LINE);
		swapSlotWithHotbar(slot, MIDDLE_LINE);
		swapSlotWithHotbar(slot, BOTTOM_LINE);
		playSwapSound(false);
	}

	public static void swapSlotToPreviousLine(int slot) {
		ANIMATION_REVERSED[slot] = true;
		swapSlotWithHotbar(slot, BOTTOM_LINE);
		swapSlotWithHotbar(slot, MIDDLE_LINE);
		swapSlotWithHotbar(slot, TOP_LINE);
		playSwapSound(true);
	}

	private static void swapLineWithHotbar(int line) {
		for (int slot = 0; slot < Inventory.getSelectionSize(); slot++) {
			swapSlotWithHotbar(slot, line);
		}
	}

	private static void swapSlotWithHotbar(int hotbarSlot, int line) {
		swapSlotWithHotbar(hotbarSlot + line * Inventory.getSelectionSize());
	}

	private static void swapSlotWithHotbar(int inventorySlot) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		MultiPlayerGameMode gameMode = minecraft.gameMode;
		if (player == null || gameMode == null || player.containerMenu != player.inventoryMenu) {
			return;
		}

		Inventory inventory = player.getInventory();
		int hotbarSlot = inventorySlot % Inventory.getSelectionSize();
		ItemStack hotbarStack = inventory.getItem(hotbarSlot);
		ItemStack inventoryStack = inventory.getItem(inventorySlot);
		if (hotbarStack.isEmpty() && inventoryStack.isEmpty()) {
			return;
		}

		gameMode.handleContainerInput(
				player.inventoryMenu.containerId,
				inventorySlot,
				hotbarSlot,
				ContainerInput.SWAP,
				player
		);

		if (!hotbarStack.isEmpty()) {
			ANIMATION_START[hotbarSlot] = getAnimationStart(inventory, hotbarSlot);
		}
	}

	private static long getAnimationStart(Inventory inventory, int slot) {
		long delaySteps = 0;
		int selectedSlot = inventory.getSelectedSlot();
		int start = Math.min(selectedSlot, slot);
		int end = Math.max(selectedSlot, slot);

		for (int index = start; index < end; index++) {
			if (!inventory.getItem(index).isEmpty()) {
				delaySteps++;
			}
		}

		return Util.getMillis() + delaySteps * TapTab.ANIMATION_DELAY;
	}

	private static void playSwapSound(boolean reversed) {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
				reversed ? TapTab.SWAP_PREV : TapTab.SWAP_NEXT,
				1.0F
		));
	}
}
