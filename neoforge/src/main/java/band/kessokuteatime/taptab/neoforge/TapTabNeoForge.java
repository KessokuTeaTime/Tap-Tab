package band.kessokuteatime.taptab.neoforge;

import band.kessokuteatime.taptab.TapTab;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(value = TapTab.ID, dist = Dist.CLIENT)
public final class TapTabNeoForge {
	public TapTabNeoForge(IEventBus modBus) {
		modBus.addListener(TapTabNeoForge::registerSounds);
		modBus.addListener(TapTabNeoForge::registerKeys);
	}

	private static void registerSounds(RegisterEvent event) {
		event.register(Registries.SOUND_EVENT, TapTab.SWAP_PREV_ID, () -> TapTab.SWAP_PREV);
		event.register(Registries.SOUND_EVENT, TapTab.SWAP_NEXT_ID, () -> TapTab.SWAP_NEXT);
	}

	private static void registerKeys(RegisterKeyMappingsEvent event) {
		KeyMapping.Category category = new KeyMapping.Category(TapTab.KEY_CATEGORY_ID);
		event.registerCategory(category);

		KeyMapping cycle = TapTab.createCycleKey(category);
		KeyMapping reverseModifier = TapTab.createReverseModifierKey(category);
		KeyMapping slotModifier = TapTab.createSlotModifierKey(category);
		event.register(cycle);
		event.register(reverseModifier);
		event.register(slotModifier);
		TapTab.setKeyMappings(cycle, reverseModifier, slotModifier);
	}
}
