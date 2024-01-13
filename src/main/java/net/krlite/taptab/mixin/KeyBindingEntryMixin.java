package net.krlite.taptab.mixin;

import net.krlite.taptab.TooltipKeyBinding;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setTooltip(Lnet/minecraft/client/gui/tooltip/Tooltip;)V"
            )
    )
    private void appendTooltip(ButtonWidget buttonWidget, Tooltip tooltip) {
        if (binding instanceof TooltipKeyBinding tooltipKeyBinding && tooltip == null) {
            buttonWidget.setTooltip(tooltipKeyBinding.tooltipSupplier().get());
        }
    }
}
