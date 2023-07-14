package net.krlite.taptab;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

public class InventorySwapper {
	public static final int HOTBAR = 0, TOP_LINE = 1, MIDDLE_LINE = 2, BOTTOM_LINE = 3;
	public static final long[] HOTBAR_SLOTS_ANIMATION_START = new long[9];
	public static final boolean[] HOTBAR_SLOTS_ANIMATION_REVERSED = new boolean[9];

	static {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_START, -1);
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_REVERSED, false);
	}

	protected static void swapSlot(@Range(from = 0, to = 35) int slot1, @Range(from = 0, to = 35) int slot2) {
		IntegratedServer server = MinecraftClient.getInstance().getServer();
		ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
		if (server == null || clientPlayer == null) return;

		ServerPlayerEntity player = server.getPlayerManager().getPlayer(clientPlayer.getUuid());
		if (slot1 == slot2 || player == null) return;

		ItemStack stack1 = player.getInventory().getStack(slot1), stack2 = player.getInventory().getStack(slot2);
		if (stack1.isEmpty() && stack2.isEmpty() || stack1 == stack2) return;

		PlayerInventory inv = player.getInventory();
		inv.setStack(slot1, stack2);
		inv.setStack(slot2, stack1);

		if (slot1 < 9 && !stack2.isEmpty()) HOTBAR_SLOTS_ANIMATION_START[slot1] = getAnimationStart(inv, slot1);
		if (slot2 < 9 && !stack1.isEmpty()) HOTBAR_SLOTS_ANIMATION_START[slot2] = getAnimationStart(inv, slot2);
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

	protected static void swapLine(@Range(from = 0, to = 3) int line1, @Range(from = 0, to = 3) int line2) {
		if (line1 == line2) return;
		int slot1 = line1 * 9, slot2 = line2 * 9;
		for (int i = 0; i < 9; i++) {
			swapSlot(slot1 + i, slot2 + i);
		}
	}

	protected static void playSwapSound(boolean reversed) {
		MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(reversed ? TapTab.Sounds.SWAP_PREV : TapTab.Sounds.SWAP_NEXT, 1.0F));
	}

	public static void swapToNextLine() {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_REVERSED, false);
		swapLine(TOP_LINE, MIDDLE_LINE);
		swapLine(TOP_LINE, BOTTOM_LINE);
		swapLine(TOP_LINE, HOTBAR);
		playSwapSound(false);
	}

	public static void swapToPrevLine() {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_REVERSED, true);
		swapLine(HOTBAR, BOTTOM_LINE);
		swapLine(HOTBAR, MIDDLE_LINE);
		swapLine(HOTBAR, TOP_LINE);
		playSwapSound(true);
	}
}
