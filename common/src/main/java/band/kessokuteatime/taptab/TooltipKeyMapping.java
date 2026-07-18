package band.kessokuteatime.taptab;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Tooltip;

import java.util.function.Supplier;

public final class TooltipKeyMapping extends KeyMapping {
	private final Supplier<Tooltip> tooltipSupplier;

	public TooltipKeyMapping(
			String translationKey,
			InputConstants.Type type,
			int code,
			Category category,
			Supplier<Tooltip> tooltipSupplier
	) {
		super(translationKey, type, code, category);
		this.tooltipSupplier = tooltipSupplier;
	}

	public Tooltip tooltip() {
		return tooltipSupplier.get();
	}
}
