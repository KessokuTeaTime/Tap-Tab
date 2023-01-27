package net.krlite.taptab.mixin;

import net.krlite.equator.math.EasingFunctions;
import net.krlite.equator.util.SystemClock;
import net.krlite.taptab.InventorySwapper;
import net.krlite.taptab.TapTabClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudAnimator {
	@Shadow private int scaledWidth;

	@ModifyArgs(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V", ordinal = 0))
	private void renderHotbarItem(Args args) {
		int x = args.get(0), y = args.get(1), slot = (x - 2 + 90 - scaledWidth / 2) / 20;
		long start = InventorySwapper.HOTBAR_SLOTS_ANIMATION_START[slot], progress = Math.max(SystemClock.queueElapsed() - start, 0);
		if (start == -1 || progress > TapTabClient.ANIMATION_DURATION) return;
		boolean reversed = InventorySwapper.HOTBAR_SLOTS_ANIMATION_REVERSED[slot];
		args.set(1, y + (int) EasingFunctions.Back.easeOut(progress, (reversed ? 1 : -1) * TapTabClient.ANIMATION_AMOUNT, (reversed ? -1 : 1) * TapTabClient.ANIMATION_AMOUNT, TapTabClient.ANIMATION_DURATION));
	}
}
