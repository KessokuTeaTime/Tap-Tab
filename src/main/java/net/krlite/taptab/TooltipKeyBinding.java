package net.krlite.taptab;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.Supplier;

public class TooltipKeyBinding extends KeyBinding {
    private final Supplier<Tooltip> tooltipSupplier;

    public TooltipKeyBinding(String translationKey, int code, String category, Supplier<Tooltip> tooltipSupplier) {
        super(translationKey, code, category);
        this.tooltipSupplier = tooltipSupplier;
    }

    public TooltipKeyBinding(String translationKey, InputUtil.Type type, int code, String category, Supplier<Tooltip> tooltipSupplier) {
        super(translationKey, type, code, category);
        this.tooltipSupplier = tooltipSupplier;
    }

    public Supplier<Tooltip> tooltipSupplier() {
        return tooltipSupplier;
    }
}
