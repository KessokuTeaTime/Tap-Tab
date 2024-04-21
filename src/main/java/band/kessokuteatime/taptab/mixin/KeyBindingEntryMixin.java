package band.kessokuteatime.taptab.mixin;

import band.kessokuteatime.taptab.TooltipKeyBinding;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;

    @Shadow @Final private ButtonWidget editButton;

    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setTooltip(Lnet/minecraft/client/gui/tooltip/Tooltip;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void appendTooltip(CallbackInfo ci) {
        if (binding instanceof TooltipKeyBinding tooltipKeyBinding) {
            editButton.setTooltip(tooltipKeyBinding.tooltipSupplier().get());
        }
    }
}
