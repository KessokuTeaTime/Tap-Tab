package band.kessokuteatime.taptab.mixin;

import band.kessokuteatime.taptab.TapTab;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void taptab$listenForInput(CallbackInfo ci) {
		TapTab.tick((Minecraft) (Object) this);
	}
}
