package dev.jorel.regexargmod;

import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class RegexArgumentType implements ArgumentType<String> {
	
	public static final void register() {
		ArgumentTypes.register("regex", RegexArgumentType.class, new RegexArgumentSerializer());
		RegexArgMod.LOGGER.info("Registered regex argument type");
	}

	public Pattern pattern;
	public String errorMessage;
	
	public RegexArgumentType(String pattern, String errorMessage) {
		this.pattern = Pattern.compile(pattern);
		this.errorMessage = errorMessage;
	}
	
	public static String getString(final CommandContext<?> context, final String name) {
		return context.getArgument(name, String.class);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String input = "";
		while(reader.canRead() && !this.pattern.matcher(input).matches()) {
			input = input + reader.read();
		}
		if(!this.pattern.matcher(input).matches()) {
			throw new SimpleCommandExceptionType(new LiteralMessage(errorMessage)).createWithContext(reader);
		}
		return input;
	}

	@Override
	public String toString() {
		return "regex(" + this.pattern.pattern() + ")(" + errorMessage + ")";
	}
	
	static class RegexArgumentSerializer implements ArgumentSerializer<RegexArgumentType> {

		@Override
		public void toPacket(RegexArgumentType argument, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeByteArray(argument.pattern.pattern().getBytes());
			packetByteBuf.writeByteArray(argument.errorMessage.getBytes());
		}

		@Override
		public RegexArgumentType fromPacket(PacketByteBuf packetByteBuf) {
			String pattern = new String(packetByteBuf.readByteArray());
			String errorMessage = new String(packetByteBuf.readByteArray());
			return new RegexArgumentType(pattern, errorMessage);
		}

		@Override
		public void toJson(RegexArgumentType argument, JsonObject jsonObject) {
			jsonObject.addProperty("pattern", argument.pattern.pattern());
			jsonObject.addProperty("message", argument.errorMessage);
		}

	}
	
}