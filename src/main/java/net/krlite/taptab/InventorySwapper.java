package net.krlite.taptab;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.krlite.equator.util.SystemClock;
import net.krlite.taptab.networking.TapTabNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

public class InventorySwapper {
	public static final int HOTBAR = 0, TOP_LINE = 1, MIDDLE_LINE = 2, BOTTOM_LINE = 3;

	public static final long[] HOTBAR_SLOTS_ANIMATION_START = new long[9];

	static {
		Arrays.fill(HOTBAR_SLOTS_ANIMATION_START, -1);
	}

	protected static void swapSlot(@Range(from = 0, to = 35) int slot1, @Range(from = 0, to = 35) int slot2) {
		if (slot1 == slot2 || MinecraftClient.getInstance().player == null) return;
		PlayerInventory inv = MinecraftClient.getInstance().player.getInventory();
		ClientPlayNetworking.send(TapTabNetworking.PLAYER_INVENTORY_SWAP_SLOTS, new PacketByteBuf(Unpooled.buffer().writeInt(slot1).writeInt(slot2)));
		if (slot1 < 9) HOTBAR_SLOTS_ANIMATION_START[slot1] = SystemClock.queueElapsed() + getAnimationDelay(slot1);
		if (slot2 < 9) HOTBAR_SLOTS_ANIMATION_START[slot2] = SystemClock.queueElapsed() + getAnimationDelay(slot2);
	}

	protected static void swapLine(@Range(from = 0, to = 3) int line1, @Range(from = 0, to = 3) int line2) {
		if (line1 == line2) return;
		int slot1 = line1 * 9, slot2 = line2 * 9;
		for (int i = 0; i < 9; i++) {
			swapSlot(slot1 + i, slot2 + i);
		}
	}

	public static void swapToNextLine() {
		swapLine(TOP_LINE, MIDDLE_LINE);
		swapLine(TOP_LINE, BOTTOM_LINE);
		swapLine(TOP_LINE, HOTBAR);
	}

	public static void swapToPrevLine() {
		swapLine(BOTTOM_LINE, HOTBAR);
		swapLine(BOTTOM_LINE, MIDDLE_LINE);
		swapLine(BOTTOM_LINE, TOP_LINE);
	}

	private static long getAnimationDelay(int slot) {
		assert MinecraftClient.getInstance().player != null;
		int selectedSlot = MinecraftClient.getInstance().player.getInventory().selectedSlot;
		long delay = 0;
		if (slot < selectedSlot) {
			for (int i = slot; i < selectedSlot; i++)
				if (!MinecraftClient.getInstance().player.getInventory().getStack(i).isEmpty())
					delay += TapTabClient.ANIMATION_DELAY;
		}
		else if (slot > selectedSlot) {
			for (int i = selectedSlot; i < slot; i++)
				if (!MinecraftClient.getInstance().player.getInventory().getStack(i).isEmpty())
					delay += TapTabClient.ANIMATION_DELAY;
		}
		return delay;
	}
}
