package net.krlite.taptab;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.krlite.taptab.networking.TapTabNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
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
		if (slot1 == slot2 || MinecraftClient.getInstance().player == null) return;
		ClientPlayNetworking.send(TapTabNetworking.PLAYER_INVENTORY_SWAP_SLOTS, new PacketByteBuf(Unpooled.buffer().writeInt(slot1).writeInt(slot2)));
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
				.play(PositionedSoundInstance.master(reversed ? TapTabClient.Sounds.SWAP_PREV : TapTabClient.Sounds.SWAP_NEXT, 1.0F));
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
