package dev.jorel.regexargmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.jorel.regexargmod.RegexArgumentType;
import net.minecraft.command.argument.ArgumentTypes;

@Mixin(ArgumentTypes.class)
public class RegexMixin {
	@Inject(at = @At("HEAD"), method = "register()V")
	private static void init(CallbackInfo info) {
		RegexArgumentType.register();
	}
}
