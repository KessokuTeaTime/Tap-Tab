package net.krlite.taptab;

import net.fabricmc.api.ModInitializer;
import net.krlite.taptab.networking.TapTabNetworking;

public class TapTab implements ModInitializer {
	@Override
	public void onInitialize() {
		TapTabNetworking.registerReceivers();
	}
}
