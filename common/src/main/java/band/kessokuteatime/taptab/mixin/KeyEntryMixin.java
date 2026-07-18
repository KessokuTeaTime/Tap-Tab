package band.kessokuteatime.taptab.mixin;

import band.kessokuteatime.taptab.TooltipKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyEntryMixin {
	@Shadow
	@Final
	private KeyMapping key;

	@Shadow
	@Final
	private Button changeButton;

	@Shadow
	private boolean hasCollision;

	@Inject(method = "refreshEntry", at = @At("TAIL"))
	private void taptab$addFallbackTooltip(CallbackInfo ci) {
		if (!hasCollision && key instanceof TooltipKeyMapping tooltipKeyMapping) {
			changeButton.setTooltip(tooltipKeyMapping.tooltip());
		}
	}
}
