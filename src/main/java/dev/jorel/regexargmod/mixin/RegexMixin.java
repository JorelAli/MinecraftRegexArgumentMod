package dev.jorel.regexargmod.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShapes;
import red.jackf.whereisit.WhereIsItClient;
import red.jackf.whereisit.client.PositionData;

@Mixin(ChatHud.class)
public class RegexMixin {
	
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"))
    public void addMessage(Text text, int messageId, CallbackInfo info) {
    	System.out.println("Received msg:" + text.getString());
    	if(!text.getString().contains("Successfully stored inventory")) {
    		return;
    	}
    	
    	String coords = text.getString().substring(text.getString().indexOf("(") + 1, text.getString().indexOf(")"));
    	String[] coordsArr = coords.split(", ");
    	int x = Integer.parseInt(coordsArr[0]);
    	int y = Integer.parseInt(coordsArr[1]);
    	int z = Integer.parseInt(coordsArr[2]);
    	
    	//Successfully stored inventory at (2227, 53, -414). 
    	
    	MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        if (world != null) {
        	int borderColour = MathHelper.clamp(0xff0000, 0, 0xffffff);
            float r = ((borderColour >> 16) & 0xff) / 255f;
            float g = ((borderColour >> 8) & 0xff) / 255f;
            float b = ((borderColour) & 0xff) / 255f;
            
            List<PositionData> positions = new ArrayList<>();
            positions.add(new PositionData(new BlockPos(x, y, z), borderColour, null, r, g, b, text));
            WhereIsItClient.handleFoundItems(positions.stream()
                .map(memory -> new PositionData(memory.pos, world.getTime(), VoxelShapes.fullCube(), r, g, b, null))
                .toList());
            if (player != null)
                player.closeHandledScreen();
        }
	}
}
