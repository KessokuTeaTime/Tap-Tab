package net.krlite.taptab;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

public class InventorySwapper {
	public static final int TOP_LINE = 1, MIDDLE_LINE = 2, BOTTOM_LINE = 3;
	public static final long[] HOTBAR_SLOTS_ANIMATION_START = new long[9];
	public static final boolean[] HOTBAR_SLOTS_ANIMATION_REVERSED = new boolean[9];

	static {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_START, -1);
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_REVERSED, false);
	}

	private static long getAnimationStart(PlayerInventory inv, int slot) {
		long start = 0;

		if (inv.selectedSlot < slot) {
			for (int i = inv.selectedSlot; i < slot; i++) {
				if (!inv.getStack(i).isEmpty()) start++;
			}
		} else if (inv.selectedSlot > slot) {
			for (int i = slot; i < inv.selectedSlot; i++) {
				if (!inv.getStack(i).isEmpty()) start++;
			}
		}

		return System.currentTimeMillis() + start * TapTab.ANIMATION_DELAY;
	}

	private static void swapSlotWithHotbar(@Range(from = 9, to = 35) int slot) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;

		int hotbarSlot = slot % 9;
		ItemStack hotbarStack = player.getInventory().getStack(hotbarSlot), stack = player.getInventory().getStack(slot);
		if (hotbarStack.isEmpty() && stack.isEmpty()) return;

		ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
		if (interactionManager == null) return;

		interactionManager.clickSlot(player.playerScreenHandler.syncId, slot, hotbarSlot, SlotActionType.SWAP, player);

		PlayerInventory inv = player.getInventory();
		if (!hotbarStack.isEmpty()) HOTBAR_SLOTS_ANIMATION_START[hotbarSlot] = getAnimationStart(inv, hotbarSlot);
	}

	private static void swapSlotWithHotbar(
			@Range(from = 0, to = 8) int slot,
			@Range(from = TOP_LINE, to = BOTTOM_LINE) int line
	) {
		swapSlotWithHotbar(slot + line * 9);
	}

	private static void swapLineWithHotbar(@Range(from = TOP_LINE, to = BOTTOM_LINE) int line) {
		for (int i = 0; i < 9; i++) swapSlotWithHotbar(i, line);
	}

	private static void playSwapSound(boolean reversed) {
		MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(reversed ? TapTab.Sounds.SWAP_PREV : TapTab.Sounds.SWAP_NEXT, 1.0F));
	}

	public static void swapToNextLine() {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_REVERSED, false);

		swapLineWithHotbar(TOP_LINE);
		swapLineWithHotbar(MIDDLE_LINE);
		swapLineWithHotbar(BOTTOM_LINE);

		playSwapSound(false);
	}

	public static void swapToPrevLine() {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_REVERSED, true);

		swapLineWithHotbar(BOTTOM_LINE);
		swapLineWithHotbar(MIDDLE_LINE);
		swapLineWithHotbar(TOP_LINE);

		playSwapSound(true);
	}

	public static void swapSlotToNextLine(@Range(from = 0, to = 8) int slot) {
		HOTBAR_SLOTS_ANIMATION_REVERSED[slot] = false;

		swapSlotWithHotbar(slot, TOP_LINE);
		swapSlotWithHotbar(slot, MIDDLE_LINE);
		swapSlotWithHotbar(slot, BOTTOM_LINE);

		playSwapSound(false);
	}

	public static void swapSlotToPrevLine(@Range(from = 0, to = 8) int slot) {
		HOTBAR_SLOTS_ANIMATION_REVERSED[slot] = true;

		swapSlotWithHotbar(slot, BOTTOM_LINE);
		swapSlotWithHotbar(slot, MIDDLE_LINE);
		swapSlotWithHotbar(slot, TOP_LINE);

		playSwapSound(true);
	}
}
