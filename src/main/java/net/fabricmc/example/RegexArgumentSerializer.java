package net.fabricmc.example;

import com.google.gson.JsonObject;

import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class RegexArgumentSerializer implements ArgumentSerializer<RegexArgumentType> {

	@Override
	public void toPacket(RegexArgumentType argument, PacketByteBuf packetByteBuf) {
		ExampleMod.LOGGER.info("Converting " + argument.pattern.pattern() + " to a packet");
		packetByteBuf.writeByteArray(argument.pattern.pattern().getBytes());
	}

	@Override
	public RegexArgumentType fromPacket(PacketByteBuf packetByteBuf) {
		ExampleMod.LOGGER.info("Parsing input packet (byte array...)...");
		String result = new String(packetByteBuf.readByteArray());

		ExampleMod.LOGGER.info("Parsed " + result);
		return new RegexArgumentType(result);
	}

	@Override
	public void toJson(RegexArgumentType argument, JsonObject jsonObject) {

		ExampleMod.LOGGER.info("Converting argument to JSON... " + argument.pattern.pattern());
		jsonObject.addProperty("pattern", argument.pattern.pattern());

		ExampleMod.LOGGER.info("Resulting JSON: " + jsonObject.toString());
	}

}