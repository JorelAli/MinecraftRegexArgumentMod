package dev.jorel.regexargmod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.include.com.google.common.io.Files;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.StringParser;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class CustomArgumentType_1_18_R2 implements ArgumentType<String> {

	static ByteClassLoader cl;
	
	public static final void register() {
		ArgumentTypes.register("custom", CustomArgumentType_1_18_R2.class, new CustomArgumentSerializer());
	}

	public Class<?> containingClass;
	public StringParser parsingFunction;

	public CustomArgumentType_1_18_R2(Class<?> containingClass, StringParser parsingFunction) {
		System.out.println("Initializing CustArg");
		this.containingClass = containingClass;
		this.parsingFunction = parsingFunction;
		System.out.println("Success!");
	}

	public static String getString(final CommandContext<?> context, final String name) {
		System.out.println("Getting string");
		return context.getArgument(name, String.class);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		System.out.println("Parsing...");
		return parsingFunction.parse(reader);
	}

	@Override
	public String toString() {
		return "custom(" + containingClass.getSimpleName() + ")";
	}

	static class CustomArgumentSerializer implements ArgumentSerializer<CustomArgumentType_1_18_R2> {

		@Override
		public void toPacket(CustomArgumentType_1_18_R2 argument, PacketByteBuf packetByteBuf) {
			System.out.println("ToPacket called");

			// We need to write two byte arrays: The class that the lambda came from and the
			// lambda info itself
			
			String path = argument.containingClass.getName().replace('.', '/') + ".class";
			InputStream stream = argument.containingClass.getClassLoader().getResourceAsStream(path);
			
			try {
				ByteArrayOutputStream lambda = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(lambda);
				oos.writeObject(argument.parsingFunction);
				oos.flush();
				
				// Write the class name
				packetByteBuf.writeByteArray(argument.containingClass.getName().getBytes());

				// Write the class
				packetByteBuf.writeByteArray(stream.readAllBytes());
				
				// Write the lambda itself
				packetByteBuf.writeByteArray(lambda.toByteArray());
				
				stream.close();
				oos.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

//		@SuppressWarnings("resource")
		@Override
		public CustomArgumentType_1_18_R2 fromPacket(PacketByteBuf packetByteBuf) {
			System.out.println("FromPacket called");
			String className = new String(packetByteBuf.readByteArray());
			System.out.println("Reading class " + className);
			byte[] classBytes = packetByteBuf.readByteArray();
			System.out.println("Read class bytes");
			try {
				Files.write(classBytes, new File("test.class"));
				System.out.println(new File("test.class").getAbsolutePath());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			// Assume reading went successfully...
			/*File file = new File("CommandAPIMain.class");
			try {
				classBytes = Files.asByteSource(file).read();
			} catch (IOException e2) {
				e2.printStackTrace();
			}*/
			
			// Load the class. This HAS to be done before we unpack the lambda
			System.out.println("Constructing classloader");
//			Class<?> parserClass = loadClass(CustomArgumentType_1_18_R2.class.getClassLoader().get, className, classBytes);
			cl = new ByteClassLoader(new URL[0], getClass().getClassLoader(), Map.of(className, classBytes));
			System.out.println("Classloader constructed. Loading...");
			Class<?> parserClass = null;
			try {
				
				parserClass = cl.findClass(className);
			} catch (ClassNotFoundException e1) {
				System.out.println("failed to find class");
				e1.printStackTrace();
			}
			System.out.println("== Class found ==");
			System.out.println("Instantiating " + parserClass);
			
			@SuppressWarnings("deprecation")
			StringParser parser = null;
			try {
				parser = (StringParser) parserClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Instantiated!");
			packetByteBuf.readByteArray();
			new Throwable().printStackTrace();
			return new CustomArgumentType_1_18_R2(parserClass, parser);
			
//			System.out.println("Found " + parserClass);
//			
//			// Read and unpack the lambda
//			ByteArrayInputStream in = new ByteArrayInputStream(packetByteBuf.readByteArray());
//			try {
//				ObjectInputStream objectInputStream = new ObjectInputStream(in);
//				System.out.println("Reading object");
//				Object readObject = (Object) objectInputStream.readObject();
//				System.out.println("Closing stream");
//				objectInputStream.close();
//				System.out.println("Printing object");
//				System.out.println(readObject);
//				System.out.println("Printing class name");
//				System.out.println(readObject.getClass().getSimpleName());
//				System.out.println("Casint object");
//				StringParser parser = (StringParser) readObject;
//				System.out.println("Parsing object");
//				return new CustomArgumentType_1_18_R2(parserClass, parser);
//			} catch (IOException | ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//
//			System.out.println("AAAAAAAAAA");
//			return null;
		}

		@Override
		public void toJson(CustomArgumentType_1_18_R2 argument, JsonObject jsonObject) {
			System.out.println("ToJSON called");
		}
		
		public Class<?> loadClass(ClassLoader classLoader, String className, byte[] classBytes) {
			try {
				Method method = classLoader.getClass().getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
				return (Class<?>) method.invoke(classLoader, className, classBytes, 0, classBytes.length);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public static class ByteClassLoader extends URLClassLoader {
		private final Map<String, byte[]> extraClassDefs;

		public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs) {
			super(urls, parent);
			System.out.println("initialized");
			this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			byte[] classBytes = this.extraClassDefs.remove(name);
			System.out.println("Finding " + name);
			if (classBytes != null) {
				System.out.println("Defining " + name);
				try {
					return defineClass(name, classBytes, 0, classBytes.length);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			try {
				return super.findClass(name);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
	}

}