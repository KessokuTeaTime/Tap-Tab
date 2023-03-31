package net.krlite.taptab.networking.receiver;

import io.netty.channel.EventLoop;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.krlite.taptab.InventorySwapper;
import net.krlite.taptab.TapTabClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.thread.ThreadExecutor;

public class PlayerInventorySwapSlots implements ServerPlayNetworking.PlayChannelHandler {
	/**
	 * Handles an incoming packet.
	 *
	 * <p>This method is executed on {@linkplain EventLoop netty's event loops}.
	 * Modification to the game should be {@linkplain ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft server instance.
	 *
	 * <p>An example usage of this is to create an explosion where the player is looking:
	 * <pre>{@code
	 * ServerPlayNetworking.registerReceiver(new Identifier("mymod", "boom"), (server, player, handler, buf, responseSender) -&rt; {
	 * 	boolean fire = buf.readBoolean();
	 *
	 * 	// All operations on the server or world must be executed on the server thread
	 * 	server.execute(() -&rt; {
	 * 		ModPacketHandler.createExplosion(player, fire);
	 *        });
	 * });
	 * }</pre>
	 *
	 * @param server         the server
	 * @param player         the player
	 * @param handler        the network handler that received this packet, representing the player/client who sent the packet
	 * @param buf            the payload of the packet
	 * @param responseSender the packet sender
	 */
	@Override
	public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int slot1 = buf.retain().readInt(), slot2 = buf.retain().readInt();
		ItemStack stack1 = player.getInventory().getStack(slot1), stack2 = player.getInventory().getStack(slot2);
		if (stack1.isEmpty() && stack2.isEmpty() || stack1 == stack2) return;
		PlayerInventory inv = player.getInventory();
		inv.setStack(slot1, stack2);
		inv.setStack(slot2, stack1);
		if (slot1 < 9 && !stack2.isEmpty()) InventorySwapper.HOTBAR_SLOTS_ANIMATION_START[slot1] = getAnimationStart(inv, slot1);
		if (slot2 < 9 && !stack1.isEmpty()) InventorySwapper.HOTBAR_SLOTS_ANIMATION_START[slot2] = getAnimationStart(inv, slot2);
	}

	private long getAnimationStart(PlayerInventory inv, int slot) {
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
		return System.currentTimeMillis() + start * TapTabClient.ANIMATION_DELAY;
	}
}
