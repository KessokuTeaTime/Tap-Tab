package net.krlite.taptab.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.krlite.taptab.networking.receiver.PlayerInventorySwapSlots;
import net.minecraft.util.Identifier;

public class TapTabNetworking {
	public static final Identifier PLAYER_INVENTORY_SWAP_SLOTS = new Identifier("taptab", "networking.player_inventory_swap_slots");

	public static void registerReceivers() {
		ServerPlayNetworking.registerGlobalReceiver(PLAYER_INVENTORY_SWAP_SLOTS, new PlayerInventorySwapSlots());
	}
}
