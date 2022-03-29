package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.RegexArgumentSerializer;
import net.fabricmc.example.RegexArgumentType;
import net.minecraft.command.argument.ArgumentTypes;

@Mixin(ArgumentTypes.class)
public class RegexMixin {
	@Inject(at = @At("HEAD"), method = "register()V")
	private static void init(CallbackInfo info) {
		ArgumentTypes.register("regex", RegexArgumentType.class, new RegexArgumentSerializer());
		
		ExampleMod.LOGGER.info("This line is printed by an example mod mixin (RegexMixin)!");
	}
}
