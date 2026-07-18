package band.kessokuteatime.taptab.fabric;

import band.kessokuteatime.taptab.TapTab;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class TapTabFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(TapTab.KEY_CATEGORY_ID);
		TapTab.setKeyMappings(
				KeyMappingHelper.registerKeyMapping(TapTab.createCycleKey(category)),
				KeyMappingHelper.registerKeyMapping(TapTab.createReverseModifierKey(category)),
				KeyMappingHelper.registerKeyMapping(TapTab.createSlotModifierKey(category))
		);
		Registry.register(BuiltInRegistries.SOUND_EVENT, TapTab.SWAP_PREV_ID, TapTab.SWAP_PREV);
		Registry.register(BuiltInRegistries.SOUND_EVENT, TapTab.SWAP_NEXT_ID, TapTab.SWAP_NEXT);
	}
}
