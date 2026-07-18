package band.kessokuteatime.taptab.mixin;

import band.kessokuteatime.taptab.InventorySwapper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Hud.class)
public abstract class HudMixin {
	@ModifyArgs(
			method = "extractItemHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Hud;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V",
					ordinal = 0
			)
	)
	private void taptab$animateHotbarSlot(Args args) {
		GuiGraphicsExtractor graphics = args.get(0);
		int x = args.get(1);
		int y = args.get(2);
		int slot = (x - 2 + 90 - graphics.guiWidth() / 2) / 20;
		if (slot >= 0 && slot < 9) {
			args.set(2, y + InventorySwapper.animationOffset(slot));
		}
	}
}
